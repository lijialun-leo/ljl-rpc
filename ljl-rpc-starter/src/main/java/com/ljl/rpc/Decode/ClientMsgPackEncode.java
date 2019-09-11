package com.ljl.rpc.Decode;

import com.ljl.rpc.client.RPCRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

public class ClientMsgPackEncode extends MessageToByteEncoder<Object>{

	@Override
	protected void encode(ChannelHandlerContext arg0, Object msg, ByteBuf out)
			throws Exception {
			RPCRequest request = (RPCRequest) msg;
			MessagePack msgPack = new MessagePack();
			byte[] raw = null;
			raw = msgPack.write(request);
			out.writeBytes(raw);	
	}
}
