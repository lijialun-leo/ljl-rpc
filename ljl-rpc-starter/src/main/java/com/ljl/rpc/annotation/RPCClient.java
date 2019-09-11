package com.ljl.rpc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
/*
* 标注哪个包作为RPC消费接口 自定义注入
* */
public @interface RPCClient {
	
}
