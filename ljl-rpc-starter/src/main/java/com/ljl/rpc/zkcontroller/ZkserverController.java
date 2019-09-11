package com.ljl.rpc.zkcontroller;


import com.ljl.rpc.config.ZkServer;
import com.ljl.rpc.pojo.Zkserver;
import com.ljl.rpc.zkServer.ZookeeperBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/service")
public class ZkserverController extends BaseController {

	@Autowired
	private ZkServer zkServer;

	@RequestMapping("information")
	@ResponseBody
	public List information(){
		List<Zkserver> serverlist = new ArrayList<Zkserver>();
		ConcurrentHashMap<String, Map<String,Map<String,String>>> parentMap =  ZookeeperBase.getServiceMap();
		Iterator<Map.Entry<String, Map<String,Map<String,String>>>> iteratorParentMap = parentMap.entrySet().iterator();
		while (iteratorParentMap.hasNext()) {
			Map.Entry<String, Map<String,Map<String,String>>> parentEntry = iteratorParentMap.next();
			Map<String,Map<String,String>> childMap = parentEntry.getValue();
			Iterator<Map.Entry<String,Map<String,String>>> childIterator = childMap.entrySet().iterator();
			while (childIterator.hasNext()) {
				Map.Entry<String,Map<String,String>>  childEntry = childIterator.next();
				Map<String,String> childsMap = childEntry.getValue();
				Iterator<Map.Entry<String,String>> childsIterator = childsMap.entrySet().iterator();
				while (childsIterator.hasNext()) {
					Map.Entry<String,String> entry = childsIterator.next();
					Zkserver zk = new Zkserver();
					zk.setServerName(parentEntry.getKey());
					zk.setIp(entry.getKey());
					zk.setStatus(entry.getValue());
					serverlist.add(zk);
				}
				
			}
		}
		return serverlist;
	}

	@RequestMapping("updateStatus")
	@ResponseBody
	public Map updateStatus(String serverName,String ip,String status){
		try {
			Boolean isTrue;
			if(status.equals("false")){
				isTrue = zkServer.zk.setData("/RPCSERVER"+"/"+serverName+"/"+ip, "true");
			}else{
				isTrue = zkServer.zk.setData("/RPCSERVER"+"/"+serverName+"/"+ip, "false");
			}
			if(isTrue){
				return toSuccessJson("成功");
			}else{
				return toSuccessJson("失败");
			}
		} catch (Exception e) {
			return toSuccessJson("失败");
		}
		
	}
	
}
