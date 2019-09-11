package com.ljl.rpc.Decode;

import com.ljl.rpc.server.RPCResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

public class ServerMsgPackEncode extends MessageToByteEncoder<Object>{

	@Override
	protected void encode(ChannelHandlerContext arg0, Object msg, ByteBuf out)
			throws Exception {
		RPCResponse RPCResponse = (RPCResponse) msg;
		MessagePack msgPack = new MessagePack();
		//msgPack.register(RPCResponse.class);
        byte[] raw = null;
        raw = msgPack.write(RPCResponse);
        out.writeBytes(raw);	
	}

}
