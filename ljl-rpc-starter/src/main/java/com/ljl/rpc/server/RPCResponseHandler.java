package com.ljl.rpc.server;


import com.ljl.rpc.client.RPCRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RPCResponseHandler extends ChannelInboundHandlerAdapter{
	
	static ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws IOException {
    	//jvm线程池
    	executorService.execute(new Runnable() {
			@Override
			public void run() {
				System.out.println("服务端接收到请求");
		    	RPCRequest request = (RPCRequest) msg;
				System.out.println(request.toString());
		        String result=InvokeServiceUtil.invoke(request);
		        RPCResponse response=new RPCResponse();
		        response.setRequestID(request.getRequestID());
		        response.setResult(result);
		        ctx.writeAndFlush(response);
			}
		});
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //flush方法再全部写到通道中
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
