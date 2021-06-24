package com.alibaba.cloud.examples.common.util.http.client.handler;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.buffer.Unpooled;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HttpClientHandler extends ChannelInboundHandlerAdapter {

    public String uri;
    public HashMap requestBoby;

    public HttpClientHandler(String uri,HashMap<String, String> requestBoby) {
        this.uri=uri;
        this.requestBoby=requestBoby;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        URI uri = new URI(this.uri);

        System.out.println("requestBoby -> "+this.requestBoby);

        Gson gson = new Gson();

        String content = gson.toJson(this.requestBoby);

        System.out.println("content -> "+content);

        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.toASCIIString(),Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8)));
        request.headers().add(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
        request.headers().add(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
        request.headers().set(HttpHeaderNames.HOST, "192.168.152.128");
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        System.out.println("request -> "+request);

        // send request
        ctx.writeAndFlush(request);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println("msg -> "+msg);
        if(msg instanceof FullHttpResponse){
            FullHttpResponse response = (FullHttpResponse)msg;
            ByteBuf buf = response.content();
            String result = buf.toString(CharsetUtil.UTF_8);
            System.out.println("response -> "+result);
        }
    }

}
