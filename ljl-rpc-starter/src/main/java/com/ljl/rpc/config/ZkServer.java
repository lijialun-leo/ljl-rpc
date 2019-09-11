package com.ljl.rpc.config;


import com.ljl.rpc.annotation.RPCServer;
import com.ljl.rpc.server.InvokeServiceUtil;
import com.ljl.rpc.task.Task;
import com.ljl.rpc.zkServer.ZookeeperBase;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Configuration
public class ZkServer  {
	@Value("${zookeeper.hosts}")
	private String ZookeeperIpHost;

	@Value("${zookeeper.baseackage}")
	private String baseackage;

	@Value("${zookeeper.robin:weight}")
	private String robin;

	@Autowired
	private RPC rpc;

	private static Map<String, String> map = new HashMap<String, String>();

	public ZookeeperBase zk;

	@Bean
	public ZkServer ZkServer(){
		ZkServer zk = new ZkServer();
		start();
		return zk;
	}

	private void start(){
		try {
			//连接zk
			zk = new ZookeeperBase(ZookeeperIpHost);
			//创建节点 获取当前项目路径
			Enumeration<URL> urls =Thread.currentThread().getContextClassLoader().getResources(baseackage.replace(".", "/"));
			while (urls.hasMoreElements()){
                URL url =  urls.nextElement();
                if(null != url){
                    String  protocol = url.getProtocol();
                    if(protocol.equals("file")){
                        String packagePath = url.getPath().replaceAll("%20"," ");//去空格

                       File file = new File(packagePath);
                       //遍历目录将服务放入map中
                       func(file,baseackage);
                    }
                }
            }
			if(!zk.nodeExists("/RPCSERVER")){
				zk.createNode("/RPCSERVER", "ROOT");
			}
			Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
	        while (it.hasNext()) {
	             Map.Entry<String, String> entry = it.next();
	             if(!zk.nodeExists("/RPCSERVER/"+entry.getKey())){
	 				zk.createNode("/RPCSERVER/"+entry.getKey(), entry.getValue());
	 			 }
	             InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
	             String hostAddress = address.getHostAddress();//192.168.0.121     
	             if(!zk.nodeExists("/RPCSERVER/"+entry.getKey()+"/"+hostAddress+":"+rpc.getPort())){
	            	 //使用临时节点当zk断开连接的时候会自动消失
	            	 zk.createNodeForTemporary("/RPCSERVER/"+entry.getKey()+"/"+hostAddress+":"+rpc.getPort(),"true");
	            	 InvokeServiceUtil.map.put(entry.getValue(), 0);
	            	 System.out.println("/RPCSERVER/"+entry.getKey()+"/"+hostAddress+":"+rpc.getPort()+" 创建");
	             }
	        }
	        //定时任务
			Task.startTask();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private static void func(File file,String packageName){
		File[] fs = file.listFiles();
		if(fs.length != 0){
			for(File f:fs){

				if(f.isDirectory())	//若是目录，则递归打印该目录下的文件
				{
					String packageName2 = packageName + "."+f.getName();
					func(f,packageName2);
				}
				if(f.isFile()){		//若是文件，直接打印
					String str = f.getName().split("\\.")[0];
					String className = packageName +"."+str;
					className = className.substring(0, className.length());
					try {
						Class implClass = Class.forName(className);
						Annotation annotation = implClass.getAnnotation(RPCServer.class);
						if(annotation != null){
							String simpleName = str;
							map.put(simpleName, className);
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
    
    public void deleteNode(){
    	try {
			ZookeeperBase zk = new ZookeeperBase(ZookeeperIpHost);
			Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
	        while (it.hasNext()) {
	             Map.Entry<String, String> entry = it.next();
	             zk.rmr("/RPCSERVER/"+entry.getKey());
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
