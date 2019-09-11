package com.ljl.rpc.Decode;


import com.ljl.rpc.server.RPCResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class ClientMsgPackDecode extends MessageToMessageDecoder<ByteBuf>{

	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf msg,
			List<Object> out) throws Exception {
		final byte[] array;
        final int length = msg.readableBytes();
        array = new byte[length];
        msg.getBytes(msg.readerIndex(), array, 0, length);
        MessagePack msgPack = new MessagePack();
        out.add(msgPack.read(array,RPCResponse.class));
	}

}
