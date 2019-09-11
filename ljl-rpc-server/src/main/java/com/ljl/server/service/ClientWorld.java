package com.ljl.server.service;

import com.ljl.rpc.annotation.RPCClient;
import com.ljl.rpc.annotation.RPCURL;

import java.util.List;



@RPCClient
public interface ClientWorld {
	@RPCURL(className="serverWorld2",methodName="message", failClassName = "fail")
	public String message(String world);

	@RPCURL(className="serverWorld2",methodName="message2", failClassName = "fail") 
	public List message2(String world);
}
