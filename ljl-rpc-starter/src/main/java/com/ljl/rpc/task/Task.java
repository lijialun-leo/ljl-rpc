package com.ljl.rpc.task;



import com.ljl.rpc.server.InvokeServiceUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2019/7/2 0002.
 */
public class Task {

    //定时任务
    public static void startTask(){
        System.out.println("*****************************************************");
        //创建计时器
        Timer timer = new Timer("开始执行任务-------------------");
        //创建计时器任务(TimerTaskBean:是自定义的类，继承了TimerTask抽象类)
        TimerTask task = new TimerTaskBean();
        //调用计时器的schedule方法（时间表），此处的60000代表：在当前时间的60000毫秒之后，此线程会被唤醒
        timer.schedule(task, 5, 60000);
        System.out.println("定时任务已启动，于5秒后执行");
        System.out.println("*****************************************************");
    }

    static class TimerTaskBean extends TimerTask {
        @Override
        public void run() {
            Map serverMap = InvokeServiceUtil.map;
            Iterator<Map.Entry<String, Integer>> it = serverMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                entry.setValue(0);
            }
        }
    }
}
