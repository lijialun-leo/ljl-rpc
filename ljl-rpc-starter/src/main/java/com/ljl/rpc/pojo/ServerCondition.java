package com.ljl.rpc.pojo;

/**
 * Created by Administrator on 2019/7/1 0001.
 */
public class ServerCondition {
    //状态 true 上线 false 下线
    private Boolean  status;
    //权重
    private int weight;
    //ip:host
    private int ip;
    //调用次数
    private int number;

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getStatus() {
        return status;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public int getIp() {
        return ip;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
