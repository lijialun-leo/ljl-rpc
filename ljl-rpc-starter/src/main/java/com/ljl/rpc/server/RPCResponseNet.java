package com.ljl.rpc.server;


import com.ljl.rpc.Decode.ServerMsgPackDecode;
import com.ljl.rpc.Decode.ServerMsgPackEncode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class RPCResponseNet {

    public static void connect(int port){
        //netty主从线程模型(建立2个线程组) 一个用于网络读写   一个用于和客户的进行连接 
        final EventLoopGroup bossGroup=new NioEventLoopGroup(2);
        final EventLoopGroup workerGroup=new NioEventLoopGroup(4);
        final EventExecutorGroup businessGroup = new DefaultEventExecutorGroup(100);
        try {
            //启动辅助类 用于配置各种参数
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)//最大排列队数
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //socketChannel.pipeline().addLast(new LineBasedFrameDecoder(8192));//以换行符为结束位置进行分包 
                            //socketChannel.pipeline().addLast(new StringDecoder());//将接收到的对象转为字符串
                        	socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2,0,2));//编码
                        	socketChannel.pipeline().addLast(new LengthFieldPrepender(2));
                        	socketChannel.pipeline().addLast(new ServerMsgPackEncode());//编码
                            socketChannel.pipeline().addLast(new ServerMsgPackDecode());//解码
                            socketChannel.pipeline().addLast(new RPCResponseHandler());//处理类
                        }
                    });
            //绑定端口 同步等待成功
            ChannelFuture future=b.bind(port).sync();
            System.out.println("netty server start on port:"+port);
            //采用非同步方法退出netty 通过异步的方法不会被阻塞
            future.channel().closeFuture().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					//释放资源退出
					System.out.println(future.channel().toString()+" 链路关闭");
		            bossGroup.shutdownGracefully();
		            workerGroup.shutdownGracefully();
				}
			});
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
