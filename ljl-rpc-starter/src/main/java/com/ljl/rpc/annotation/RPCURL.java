package com.ljl.rpc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
/* 消费服务注解
* className 调用service名称
* methodName 调用方法名称
* failClassName 熔断方法
* */
public @interface RPCURL {
	String className();
	String methodName();
	String failClassName() default "";
}
