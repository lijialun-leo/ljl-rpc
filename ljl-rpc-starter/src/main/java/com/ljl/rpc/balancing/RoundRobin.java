package com.ljl.rpc.balancing;

import java.util.List;

/*
* 简单轮询
*
*
* */
public class RoundRobin {
	
	 private static Integer pos = 0;

	 public static String getServer(List<String> list){
	 	String server = null;
        synchronized (pos)
        {
            if (pos > (list.size()-1))
                pos = 0;
            server = list.get(pos);
            pos ++;
        }
        return server;
	 }

}
