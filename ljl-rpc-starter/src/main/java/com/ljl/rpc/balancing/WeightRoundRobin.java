package com.ljl.rpc.balancing;



import com.ljl.rpc.pojo.ServerCondition;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Administrator on 2019/7/1 0001.
 */
public class WeightRoundRobin {
    private List<ServerCondition> servers;
    private int currentIndex;
    private int totalServer;
    private int currentWeight;
    private int maxWeight;
    private int gcdWeight;

    public WeightRoundRobin() {
        totalServer = servers.size();
        currentIndex = totalServer - 1;
        maxWeight = maxWeight();
        gcdWeight = serverGcd();
    }

    public ServerCondition round() {
        while (true) {
            currentIndex = (currentIndex + 1) % totalServer;
            if (currentIndex == 0) {
                currentWeight = currentWeight - gcdWeight;
                if (currentWeight <= 0) {
                    currentWeight = maxWeight;
                    if(currentWeight == 0) {
                        return null;
                    }
                }
            }

            if(servers.get(currentIndex).getWeight() >= currentWeight) {
                return servers.get(currentIndex);
            }
        }
    }
    /**
     * 返回所有服务器的权重的最大公约数
     *
     * @return
     */
    private int serverGcd() {
        int comDivisor = 0;
        for (int i = 0; i < totalServer - 1; i++) {
            if (comDivisor == 0) {
                comDivisor = gcd(servers.get(i).getWeight(), servers.get(i + 1).getWeight());
            } else {
                comDivisor = gcd(comDivisor, servers.get(i + 1).getWeight());
            }
        }
        return comDivisor;
    }

    /**
     * 获得服务器中的最大权重
     *
     * @return
     */
    private int maxWeight() {
        int max = servers.get(0).getWeight();
        int tmp;
        for (int i = 1; i < totalServer; i++) {
            tmp = servers.get(i).getWeight();
            if (max < tmp) {
                max = tmp;
            }
        }
        return max;
    }

    /**
     * 求两个数的最大公约数 4和6最大公约数是2
     *
     * @param num1
     * @param num2
     * @return
     */
    private int gcd(int num1, int num2) {
        BigInteger i1 = new BigInteger(String.valueOf(num1));
        BigInteger i2 = new BigInteger(String.valueOf(num2));
        return i1.gcd(i2).intValue();
    }

}
