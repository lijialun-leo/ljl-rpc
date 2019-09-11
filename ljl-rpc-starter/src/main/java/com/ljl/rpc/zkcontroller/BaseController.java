package com.ljl.rpc.zkcontroller;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class BaseController {
	

	public Map toSuccessJson(String msg){
		Map result=new HashMap();
		    result.put("success", true);
	        result.put("msg",msg);
	        return result;
	}

	public Map toErrorJson(String msg){
		Map result=new HashMap();
		 result.put("success", false);
	        result.put("msg",msg);
	        return result;
	}
	@Autowired
	private HttpServletRequest request;


	protected HttpServletRequest getRequest() {
		return request;
	}


	protected void setRequest(HttpServletRequest request) {
		this.request = request;
	}



	protected Object message(String key, Object value){
		Map m = new HashMap();
		m.put(key, value);
		return m;
	}


	protected Object message(Object value){
		Map m = new HashMap();
		m.put("message", value);
		return m;
	}

	protected Object success(String msg){
		Map m = new HashMap();
		m.put("success", msg);
		return m;
	}
	
	public Map SuccessJson(Object data){
		Map result=new HashMap();
		    result.put("success", true);
	        result.put("msg",data);
	        return result;
	}
}
