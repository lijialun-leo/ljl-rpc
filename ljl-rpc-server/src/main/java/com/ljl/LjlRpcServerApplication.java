package com.ljl;

import com.ljl.rpc.annotation.RPCClientPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RPCClientPackage(basePackage = "com.ljl.server.service")
public class LjlRpcServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LjlRpcServerApplication.class, args);
	}

}
