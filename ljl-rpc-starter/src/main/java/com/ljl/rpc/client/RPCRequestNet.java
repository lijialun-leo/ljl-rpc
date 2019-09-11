package com.ljl.rpc.client;


import com.ljl.rpc.Decode.ClientMsgPackDecode;
import com.ljl.rpc.Decode.ClientMsgPackEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.net.InetSocketAddress;

public class RPCRequestNet {
	//管理以ip:端口号为key的连接池   FixedChannelPool继承SimpleChannelPool，有大小限制的连接池实现
	public static ChannelPoolMap<InetSocketAddress, FixedChannelPool> poolMap;
	//启动辅助类 用于配置各种参数
	private static Bootstrap b=new Bootstrap();
	//单利
	private static RPCRequestNet rpcClient = new RPCRequestNet();
	
	static {
		b.group(new NioEventLoopGroup())
		.channel(NioSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY,true);//禁止使用Nagle算法 作用小数据即时传输
    }
	
	public static RPCRequestNet getRPCRequestNet(){
		return rpcClient;
	}
	
	private RPCRequestNet(){
		init();
	}
	
	public void init() {
        poolMap = new AbstractChannelPoolMap<InetSocketAddress, FixedChannelPool>() {
			@Override
			protected FixedChannelPool newPool(InetSocketAddress inetSocketAddress) {
				ChannelPoolHandler handler = new ChannelPoolHandler() {
					//使用完channel需要释放才能放入连接池
					@Override
					public void channelReleased(Channel ch) throws Exception {
						System.out.println("channelReleased. Channel ID: " + ch.id());
					}
					//当链接创建的时候添加channelhandler，只有当channel不足时会创建，但不会超过限制的最大channel数
					@Override
					public void channelCreated(Channel ch) throws Exception {
						System.out.println("channelCreated. Channel ID: " + ch.id());
						ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2,0,2));
						ch.pipeline().addLast(new LengthFieldPrepender(2));
						ch.pipeline().addLast(new ClientMsgPackEncode());//编码
						ch.pipeline().addLast(new ClientMsgPackDecode());//解码
						ch.pipeline().addLast(new RPCRequestHandler());//添加相应回调处理和编解码器
					}
					//获取连接池中的channel
					@Override
					public void channelAcquired(Channel ch) throws Exception {
						 System.out.println("channelAcquired. Channel ID: " + ch.id());
					}
				};
				return new FixedChannelPool(b.remoteAddress(inetSocketAddress), handler, ChannelHealthChecker.ACTIVE, null, -1L, 5, 2147483647, true, false); //单个服务端连接池大小
			}
		};
    }

    public void connect(String host,int port,final RPCRequest request){
    	InetSocketAddress addr = new InetSocketAddress(host, port);
    	final SimpleChannelPool pool = RPCRequestNet.poolMap.get(addr);
    	final Future<Channel> f = pool.acquire();
    	f.addListener(new FutureListener<Channel>() {
			@Override
			public void operationComplete(Future<Channel> arg0) throws Exception {
				  if (f.isSuccess()) {
				      Channel ch = f.getNow();
				      ch.writeAndFlush(request);
					  System.out.println("发送请求");
					  pool.release(ch);
				  }
			}
		});
    	synchronized (request) {
        	//因为异步 所以不阻塞的话 该线程获取不到返回值
            //放弃对象锁 并阻塞等待notify
            try {
				request.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
