package com.ljl.rpc.ProxyFactory;


import com.ljl.rpc.client.RPCProxyHandler;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class MethodProxyFactory<T> implements FactoryBean<T> {
	private Class<T> interfaceClass;//所对应的消费接口
	
    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }
	
    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
    @Override
    public T getObject() throws Exception {
    	return (T) newInstance(interfaceClass);//通过对应的消费接口返回代理类
    }
    
    @Override
    public Class<?> getObjectType() {
    	return interfaceClass;
    }
    
    @Override
    public boolean isSingleton() {
    	return true;
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> methodInterface) {
        final RPCProxyHandler<T> methodProxy = new RPCProxyHandler<T>();
        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(), 
                new Class[]{methodInterface}, 
                methodProxy);
    }

}
