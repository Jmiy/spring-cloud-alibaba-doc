package com.alibaba.cloud.examples.common.util.http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;

import com.alibaba.cloud.examples.common.util.http.client.handler.HttpClientHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Component
public class HttpClient {
    public static void start(String host,int port){
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {

            HashMap map = new HashMap<>();
            map.put("type", "country");

            String url = "/api/common/dict/select";

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel channel)
                                throws Exception {
//                            channel.pipeline().addLast(new HttpRequestEncoder());// 客户端对发送的httpRequest进行编码
//                            channel.pipeline().addLast(new HttpResponseDecoder());// 客户端需要对服务端返回的httpresopnse解码
                            channel.pipeline().addLast(new HttpClientCodec());
                            channel.pipeline().addLast(new HttpObjectAggregator(65536));
                            channel.pipeline().addLast(new HttpContentDecompressor());
                            channel.pipeline().addLast(new HttpClientHandler(url,map));
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();

//            .channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            socketChannel.pipeline().addLast(new HttpRequestEncoder());// 客户端对发送的httpRequest进行编码
//                            socketChannel.pipeline().addLast(new HttpResponseDecoder());// 客户端需要对服务端返回的httpresopnse解码
//                            socketChannel.pipeline().addLast(new StartTestResponse());
//                        }
//                    });

//            Channel channel = bootstrap.connect("127.0.0.1", 8775).sync().channel();
//
//
//            // *** 生成post传送的uri
//            URI uri = new URI("/scan/" + taskid + "/start");
//
//            // *** 设置POST数据包中传输的数据 ***
//            String content = "hello post";
//
//            FullHttpRequest requestToSQLMAPAPI = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
//                    uri.toASCIIString(), Unpooled.wrappedBuffer(content.getBytes("UTF-8")));
//
//            requestToSQLMAPAPI.headers().set(HttpHeaders.Names.HOST, "127.0.0.1");
//            requestToSQLMAPAPI.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
//            requestToSQLMAPAPI.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
//                    requestToSQLMAPAPI.content().readableBytes());
//            requestToSQLMAPAPI.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");

            // headers.set("Host", "127.0.0.1");
            // headers.set("Connection", HttpHeaderValues.CLOSE);
            // headers.set("Content-Type", "application/json");
            // headers.set("Content-Length", "" + contentByteBuf.capacity());
            // headers.set("User-Agent", "Python-urllib/2.7");
            // headers.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP + "," +
            // HttpHeaderValues.DEFLATE);
            // headers.set(HttpHeaderNames.ACCEPT_CHARSET,
            // "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            // headers.set(HttpHeaderNames.ACCEPT_LANGUAGE, "fr");
            // headers.set(HttpHeaderNames.USER_AGENT, "Netty Simple Http Client side");
            // headers.set(HttpHeaderNames.ACCEPT,
            // "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            // headers.set(HttpHeaderNames.HOST, "127.0.0.1:8775");
            // headers.set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            // headers.set(HttpHeaderNames.CONNECTION, "close");
            // headers.set(HttpHeaderNames.USER_AGENT, "Python-urllib/2.7");

            // *
            // *** 输出requestToSQLMAPAPI的内容
//            System.out.println("---requestToSQLMAPAPI---");
//            System.out.println(requestToSQLMAPAPI.toString());
//            System.out.println();
//            System.out.println(requestToSQLMAPAPI.content().toString(0, requestToSQLMAPAPI.content().capacity(),
//                    Charset.defaultCharset())); //
//            // */
//
//            // send request
//            channel.writeAndFlush(requestToSQLMAPAPI).sync();
//            channel.closeFuture().sync();


        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            group.shutdownGracefully();
        }
    }
}
