package com.ljl.rpc.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljl.rpc.client.RPCRequest;
import com.ljl.rpc.server.RPCResponse;
import com.ljl.rpc.server.RPCResponseNet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Configuration
@ConfigurationProperties(prefix="netty")
public class RPC {

    private static ObjectMapper objectMapper=new ObjectMapper();

    public void setPort(int port) {
        RPC.port = port;
    }

    private static int port;

    public int getPort() {
    	return port;
    }

    @Bean
    public RPC rpc(){
        RPC rpc = new RPC();
        Initialized();
        return rpc;
    }

    private void Initialized() {
    	//初始化方法 不开线程会和tomcat 冲突
    	new Thread() {
    		public void run() {
    			RPC.start();
    		}
    	}.start();//开启线程
    }

    /**
     * 实现端启动RPC服务
     */
    public static void start(){
        RPCResponseNet.connect(port);
    }
    

    public static String requestEncode(RPCRequest request) throws JsonProcessingException {
    	//System.getProperty("line.separator") == 换行符 主要目的是为了和netty的LineBasedFrameDecoder连用 防止粘包和拆包
        return objectMapper.writeValueAsString(request)+System.getProperty("line.separator");
    }

    public static RPCRequest requestDeocde(String json) throws IOException {
        return objectMapper.readValue(json,RPCRequest.class);
    }

    public static String responseEncode(RPCResponse response) throws JsonProcessingException {
    	//System.getProperty("line.separator") == 换行符 主要目的是为了和netty的LineBasedFrameDecoder连用 防止粘包和拆包
        return objectMapper.writeValueAsString(response)+System.getProperty("line.separator");
    }

    public static Object responseDecode(String json) throws IOException {
        return objectMapper.readValue(json,RPCResponse.class);
    }

}
