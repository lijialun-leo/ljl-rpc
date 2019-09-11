package com.ljl.rpc.annotation;

import com.ljl.rpc.handle.ClientPackageScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ClientPackageScan.class)
@Documented
/*
* 标注哪个包作为RPC消费接口 自定义注入
* */
public @interface RPCClientPackage {
    String[] basePackage() default {};
}
