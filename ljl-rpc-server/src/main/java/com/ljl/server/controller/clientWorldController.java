package com.ljl.server.controller;

import java.util.List;


import com.ljl.server.service.ClientWorld;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/clientWorld")
@Api(value="clientWorldController",tags={"RPC测试接口"})
public class clientWorldController {
	
	@Autowired
	private ClientWorld clientWorld;
	
	@RequestMapping("sendMessage")
	@ApiOperation(value="RPC测试", httpMethod="GET", notes="RPC测试", produces="application/json; charset=utf-8")
	@ResponseBody
	public String sendMessage(@ApiParam(required=false,value="message") @RequestParam(value="message",required=true)String message){
		return clientWorld.message(message);
	}

	@RequestMapping("sendMessage2")
	@ApiOperation(value="RPC测试2", httpMethod="GET", notes="RPC测试2", produces="application/json; charset=utf-8")
	@ResponseBody
	public List sendMessage2(@ApiParam(required=false,value="message") @RequestParam(value="message",required=true)String message){
		return clientWorld.message2(message);
	}
}
