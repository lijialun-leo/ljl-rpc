package com.ljl.server.service.Impl;

import java.util.ArrayList;
import java.util.List;


import com.ljl.rpc.annotation.RPCServer;
import com.ljl.server.service.AddressUrl;
import org.springframework.stereotype.Service;

@Service
@RPCServer
public class serverWorld2 {
	
	public String message(String world){
		return "Hello world";
	} 
	
	public List message2(String world){
		List<AddressUrl> list = new ArrayList<AddressUrl>();
		for (int i = 0; i < 2; i++) {
			AddressUrl addressUrl = new AddressUrl();
			addressUrl.setAid(i);
			list.add(addressUrl);
		}
		return list;
	} 

}
