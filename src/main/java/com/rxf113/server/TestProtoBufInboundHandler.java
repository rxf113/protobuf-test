package com.rxf113.server;

import com.rxf113.MsgProtos;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 打印消息 并相应
 *
 * @author rxf113
 */
public class TestProtoBufInboundHandler extends SimpleChannelInboundHandler<MsgProtos.Msg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgProtos.Msg msg) throws Exception {
        System.out.printf("time : %s   type : %d  content : %s\n", msg.getTime(), msg.getType(), msg.getContent());

        //响应消息
        MsgProtos.Msg.Builder builder = MsgProtos.Msg.newBuilder();
        builder.setType(2);
        builder.setContent("好的客户端 i received ");
        builder.setTime(String.valueOf(System.currentTimeMillis()));
        MsgProtos.Msg build = builder.build();

        ctx.channel().writeAndFlush(build);
    }
}
