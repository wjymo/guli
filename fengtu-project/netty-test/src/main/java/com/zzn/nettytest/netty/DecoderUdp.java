package com.zzn.nettytest.netty;

import com.zzn.nettytest.bean.MessageInfo;
import com.zzn.nettytest.constant.NameSpaceEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class DecoderUdp extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
        ByteBuf in = datagramPacket.content();
        String str=in.toString(CharsetUtil.UTF_8);
//        ByteBuf bf = Unpooled.copyShort(in.readShortLE(), in.readShortLE());//起始符 报文属性 4
//        ByteBuf bf = Unpooled.copyDouble();
        int messageLen = in.readInt();
        ByteBuf byteBuf = in.readBytes(messageLen);
        String content = byteBuf.toString(CharsetUtil.UTF_8);
//        CharSequence charSequence = in.readCharSequence(messageLen, CharsetUtil.UTF_8);
//        String message = charSequence.toString();
        int namespaceCode = in.readInt();
        String namespace = NameSpaceEnum.getNamespaceByCode(namespaceCode);
        MessageInfo messageInfo=new MessageInfo().setContent(content).setNamespace(namespace);
        out.add(messageInfo);

//        bf.writeInt(in.readIntLE());//报文类型 4
//        bf.writeShort(in.readShortLE());//长度 2
//        bf.writeInt(in.readIntLE());//时戳 4
//        final byte[] sums = new byte[8];//数字量
//        in.readBytes(sums);
//        bf.writeBytes(sums);
//        while (in.readableBytes() > 4) {//模拟量
//            bf.writeFloat(in.readIntLE());
//        }
//        bf.writeShort(in.readShortLE());//效验
//        bf.writeShort(in.readShortLE());//结束符
//        out.add(new UdpRequest(bf, datagramPacket));
    }
}
