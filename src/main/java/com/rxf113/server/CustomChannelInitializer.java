package com.rxf113.server;

import com.google.protobuf.MessageLiteOrBuilder;
import com.rxf113.MsgProtos;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.List;

/**
 * 初始化各个通道
 *
 * @author rxf113
 */
public class CustomChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(new HttpServerCodec());
        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
        socketChannel.pipeline().addLast(new HttpObjectAggregator(1024 * 64));

        socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));

        //将WebSocketFrame转为ByteBuf 以便后面的 ProtobufDecoder 解码
        socketChannel.pipeline().addLast(new MessageToMessageDecoder<WebSocketFrame>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
                ByteBuf byteBuf = frame.content();
                byteBuf.retain();
                out.add(byteBuf);
            }
        });

        socketChannel.pipeline().addLast(new ProtobufDecoder(MsgProtos.Msg.getDefaultInstance()));
        //自定义入站处理
        socketChannel.pipeline().addLast(new TestProtoBufInboundHandler());

        //出站处理 将protoBuf实例转为WebSocketFrame
        socketChannel.pipeline().addLast(new ProtobufEncoder() {
            @Override
            protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
                MsgProtos.Msg mpMsg = (MsgProtos.Msg) msg;
                WebSocketFrame frame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(mpMsg.toByteArray()));
                out.add(frame);
            }
        });
    }
}