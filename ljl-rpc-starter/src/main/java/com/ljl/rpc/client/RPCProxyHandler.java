package com.ljl.rpc.client;

import com.google.gson.Gson;
import com.ljl.rpc.annotation.RPCURL;
import com.ljl.rpc.balancing.RoundRobin;
import com.ljl.rpc.fail.FailBack;
import com.ljl.rpc.util.ApplicationContextProvider;
import com.ljl.rpc.zkServer.ZookeeperBase;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RPCProxyHandler<T> implements InvocationHandler {
    public static Map requestLockMap=new ConcurrentHashMap<String,RPCRequest>();;//全局map 每个请求对应的锁 用于同步等待每个异步的RPC请求
    private AtomicLong requestTimes=new AtomicLong(0);//记录调用的次数 也作为ID标志
    @Autowired
    private static ApplicationContextProvider applicationContex;

    @Override
    public Object invoke(Object proxy, Method method, Object[] obj) throws Throwable {
        RPCRequest request=new RPCRequest();
        request.setRequestID(buildRequestID(method.getName()));
        //获取调用方法的ClassName和MethodName
        RPCURL url = method.getAnnotation(RPCURL.class);
        Map serverMap = ZookeeperBase.getServiceMap().get(url.className());
        Iterator<Map.Entry<String, Map<String,String>>> it = serverMap.entrySet().iterator();
        while (it.hasNext()) {
             Map.Entry<String, Map<String,String>> entry = it.next();
             String className = entry.getKey();
             Map<String,String> serverList = entry.getValue();
             Iterator<Map.Entry<String,String>> serverListIt = serverList.entrySet().iterator();
             List<String> list = new ArrayList<String>();
             while (serverListIt.hasNext()) {
            	 Map.Entry<String,String> ipEntry = serverListIt.next();
            	 list.add(ipEntry.getKey());
             }
             String ipAndHost = RoundRobin.getServer(list);//后期需要添加负载均衡策略(已有轮询)
             String isTrue = serverList.get(ipAndHost);
             if(isTrue.equals("true")){
	            	 String [] strList = new String[obj.length];
		            	 for (int i = 0; i < obj.length; i++) {
		            		 strList[i] = obj[i].toString();
	            	 }
            		 String str[] = ipAndHost.split(":");
            		 request.setClassName(className);
            		 request.setMethodName(url.methodName());
            		 request.setParameters(strList);
            		 requestLockMap.put(request.getRequestID(),request);
            		 RPCRequestNet.getRPCRequestNet().connect(str[0], Integer.parseInt(str[1]),request);
            		 requestLockMap.remove(request.getRequestID());
            		 Type returnType =  method.getGenericReturnType();
            		 Class typeClass = Class.forName(returnType.toString().split(" ")[1].trim());
            		 Gson gson = new Gson();
            		 return gson.fromJson(request.getResult(), typeClass);
             }else{
            	 FailBack failBack = (FailBack) applicationContex.getBean(url.failClassName());
            	 return failBack.failBack();
             }
        }
        return "找不到服务";
    }
    
    //生成请求的唯一ID
    private String buildRequestID(String methodName){
        StringBuilder sb=new StringBuilder();
        sb.append(requestTimes.incrementAndGet());
        sb.append(System.currentTimeMillis());
        sb.append(methodName);
        Random random = new Random();
        sb.append(random.nextInt(1000));
        return sb.toString();
    }
}
