package com.ljl.rpc.zkServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class ZookeeperBase implements Watcher {
	//服务列表
	private static ConcurrentHashMap<String, Map<String,Map<String,String>>> serviceMap = new ConcurrentHashMap<String,Map<String,Map<String,String>>>();

	private static final int SESSION_TIME_OUT = 2000;

	private static final String basePath = "/request";

	private CountDownLatch countDownLatch = new CountDownLatch(1);

	private static ZooKeeper zookeeper = null;

	public static ConcurrentHashMap<String, Map<String, Map<String, String>>> getServiceMap() {
		return serviceMap;
	}

	public void process(WatchedEvent event) {
		// 事件类型
        EventType eventType = event.getType();
        // 受影响的path
        String path = event.getPath();
		if(EventType.None == eventType){
			System.out.println("连接成功");
			//初始化服务列表 
			try {
				intiServerMap("/RPCSERVER");
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			countDownLatch.countDown();
		}else if(EventType.NodeChildrenChanged == eventType){//某节点的其子节点有变化
			String paths[] = path.split("/");
			Map<String,Map<String,String>> Map = serviceMap.get(paths[3]);
			try {
				if(Map != null){
					//目前列表中数据
					Map<String,String> childMap = Map.get(new String(getData("/"+paths[2]+"/"+paths[3])));
					//节点数据 注册监听
					List<String> childrens = getChilds("/"+paths[2]+"/"+paths[3]);
					if(childMap.size() == childrens.size()){
						System.out.println(childrens.size());
					}else{
						//上线处理
						if(childrens.size() !=0 && childrens.size() > childMap.size()){
							for (String str : childrens) {
								if(!childMap.containsKey(str)){
									childMap.put(str, "true");
									System.out.println("服务上线"+path+"/"+str);
									getDataByWatch("/"+paths[2]+"/"+paths[3]+"/"+str);//注册watch
								}
							}
						}
						//下线处理
						if(childMap.size() != 0 && childMap.size() > childrens.size()){
							Iterator<Map.Entry<String, String>> it = childMap.entrySet().iterator();
							while (it.hasNext()) {
								Map.Entry<String, String> entry = it.next();
								if(!childrens.contains(entry.getKey())){
									it.remove();
									System.out.println("服务下线"+path+"/"+entry.getKey());
								}
							}
						}
					}
				}else{
					Map<String,Map<String,String>> map = new HashMap<String, Map<String,String>>();
					Map<String,String> childMap = new HashMap<String, String>();
					map.put(new String(getData("/"+paths[2]+"/"+paths[3])), childMap);
					serviceMap.put(paths[3], map);
					getChilds("/"+paths[2]+"/"+paths[3]);//注册监听
				}
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else if(EventType.NodeDataChanged == eventType){//某节点的数据发生改变 节点注册
			System.out.println(path);
			String paths[] = path.split("/");
			Map<String,Map<String,String>> Map = serviceMap.get(paths[3]);
			Map<String, String> childMap;
			try {
				childMap = Map.get(new String(getData("/"+paths[2]+"/"+paths[3])));
				Iterator<Map.Entry<String, String>> it = childMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> entry = it.next();
					if(paths[4].equals(entry.getKey())){
						String isTrue = new String(getDataByWatch("/"+paths[2]+"/"+paths[3]+"/"+paths[4]));
						if(isTrue.equals("false")){
							entry.setValue("false");
							System.out.println("/"+paths[2]+"/"+paths[3]+"/"+paths[4]+"降级");
						}else{
							entry.setValue("true");
							System.out.println("/"+paths[2]+"/"+paths[3]+"/"+paths[4]+"升级");
						}
					}
				}
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
 
	public ZookeeperBase(String host) throws IOException, InterruptedException{
		this.zookeeper = new ZooKeeper(host, SESSION_TIME_OUT, this);
		countDownLatch.await();
	}
	
	
	//==================== 工具函数  ==========================
	public String pathChange(String path){
		if(path.startsWith(ZookeeperBase.basePath)){
			return path;
		}else{
			return ZookeeperBase.basePath + path;
		}
	}
	
	//===================== 节点操作函数 ==========================
	//node是否存在
	public Boolean nodeExists(String path) throws KeeperException, InterruptedException{
		path = this.pathChange(path);
		Stat stat = this.zookeeper.exists(path, true);
		return stat == null ? false : true;
	}
	//创建临时node
	public Boolean createNodeForTemporary(String path, String data) throws KeeperException, InterruptedException{
		path = this.pathChange(path);
		if(!this.nodeExists(path)) {
			String listPath[] = path.split("/");
			String prePath = "";
			for(int i=1; i<listPath.length-1; i++){
				prePath = prePath + "/" + listPath[i];
				if(!this.nodeExists(prePath)){
					this.zookeeper.create(prePath, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				}
			}
			this.zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			return true;
		}else{
			return false;
		}
	}
	
	//创建node
	public Boolean createNode(String path, String data) throws KeeperException, InterruptedException{
		path = this.pathChange(path);
		if(!this.nodeExists(path)) {
			String listPath[] = path.split("/");
			String prePath = "";
			for(int i=1; i<listPath.length-1; i++){
				prePath = prePath + "/" + listPath[i];
				if(!this.nodeExists(prePath)){
					this.zookeeper.create(prePath, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);	
				}
			}
			this.zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			return true;
		}else{
			return false;
		}
	}
	//获取node内容
	public String getData(String path) throws KeeperException, InterruptedException{
		path = this.pathChange(path);
		if(this.nodeExists(path)) {
			 return new String(this.zookeeper.getData(path, false, null));
		}else{
			return null;
		}
	}
	
	//获取node内容 注册节点
	public String getDataByWatch(String path) throws KeeperException, InterruptedException{
		path = this.pathChange(path);
		if(this.nodeExists(path)) {
			 return new String(this.zookeeper.getData(path, true, null));
		}else{
			return null;
		}
	}
	
	//设置node内容
	public Boolean setData(String path, String data) throws KeeperException, InterruptedException{
		path = this.pathChange(path);
		if(this.nodeExists(path)){		
			this.zookeeper.setData(path, data.getBytes(), -1);	
			return true;
		}else{
			return false;
		}
	}
	//删除node
	public Boolean delNode(String path) throws InterruptedException, KeeperException{
		path = this.pathChange(path);
		if(this.nodeExists(path)){
			this.zookeeper.delete(path, -1);
			return true;
		}else{
			return false;
		}
	}
	//获取子节点
	public List<String> getChilds(String path) throws KeeperException, InterruptedException{
		path = this.pathChange(path);
		if(this.nodeExists(path)){
			return this.zookeeper.getChildren(path, true);
		}else{
			return null;
		}
	}
	//获取子节点数量
	public Integer getChildsNum(String path) throws KeeperException, InterruptedException{
		path = this.pathChange(path);
		if(this.getChilds(path) == null){
			return null;
		}else{
			return this.getChilds(path).size();
		}
	}
	//关闭连接
	public void closeConnection() throws InterruptedException{
		if(this.zookeeper != null){
			zookeeper.close();
		}
	}
	//删除节点
	public void rmr(String path) throws Exception {
        //获取路径下的节点
		path = this.pathChange(path);
        List<String> children = this.zookeeper.getChildren(path, false);
        for (String pathCd : children) {
            //获取父节点下面的子节点路径
            String newPath = "";
            //递归调用,判断是否是根节点
            if (path.equals("/")) {
                newPath = "/" + pathCd;
            } else {
                newPath = path + "/" + pathCd;
            }
            rmr(newPath);
        }
        //删除节点,并过滤zookeeper节点和 /节点
        if (path != null && !path.trim().startsWith("/zookeeper") && !path.trim().equals("/")) {
        	this.zookeeper.delete(path, -1);
            //打印删除的节点路径
            System.out.println("被删除的节点为：" + path);
        }
    }
	
	//初始化服务列表
	private  void intiServerMap(String path) throws KeeperException, InterruptedException{
        List<String> children = getChilds(path);
        if(children.size() !=0){
        	for (String pathCd : children) {
        		Map<String,Map<String,String>> parentMap = new HashMap<String, Map<String,String>>();
        		Map<String,String> childList = new HashMap<String,String>();
        		List<String> childrens = getChilds(path+"/"+pathCd);
        		String parentContext =  new String(getData(path+"/"+pathCd));
        		if(childrens.size() !=0){
        			for (String child : childrens) {
        				//注册节点watch 用于服务降级用
        				String childContext =  new String(getDataByWatch(path+"/"+pathCd+"/"+child));
        				childList.put(child, childContext);
        			}
        		}
        		parentMap.put(parentContext, childList);
        		serviceMap.put(pathCd, parentMap);
        	}
        }
        
        ObjectMapper objectMapper=new ObjectMapper();
        try {
			System.out.println(objectMapper.writeValueAsString(serviceMap));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
