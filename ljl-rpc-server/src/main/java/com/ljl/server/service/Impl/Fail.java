package com.ljl.server.service.Impl;

import com.ljl.rpc.fail.FailBack;
import org.springframework.stereotype.Service;


@Service
public class Fail implements FailBack {

	@Override
	public String failBack() {
		return "管理员跑路了";
	}

}
