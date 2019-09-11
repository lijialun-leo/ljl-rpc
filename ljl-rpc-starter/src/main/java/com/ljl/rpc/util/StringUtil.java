package com.ljl.rpc.util;

public class StringUtil {

	//首字符转小写
	 public static String toLowerCaseFirstOne(String s){
	        if(Character.isLowerCase(s.charAt(0)))
	            return s;
	        else
	            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	    }

}
