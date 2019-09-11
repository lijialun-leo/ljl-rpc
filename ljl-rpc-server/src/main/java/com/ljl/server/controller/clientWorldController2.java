package com.ljl.server.controller;

import java.util.List;

import com.ljl.server.service.ClientWorld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/clientWorld2")
public class clientWorldController2 {
	
	@Autowired
	private ClientWorld clientWorld;
	
	@RequestMapping("sendMessage")
	@ResponseBody
	public String sendMessage(String message){
		return clientWorld.message(message);
	}

}
