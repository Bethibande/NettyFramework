package de.bethibande.netty;

import de.bethibande.netty.channels.NettyPacketChannel;
import de.bethibande.netty.server.NettyServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;

public class Test {

    public static class TestClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ByteBuf buf = Unpooled.buffer();
            buf.writeInt(3);
            buf.writeCharSequence("Hello World!", StandardCharsets.UTF_8);

            final ChannelFuture cf = ctx.writeAndFlush(buf);
            cf.addListener((ChannelFutureListener) channelFuture -> {
                System.out.println("Sent!");
                assert channelFuture == cf;
                ctx.close();
            });
        }
    }

    public static void main(String[] args) {
        NettyServer s = new NettyServer();
        s.setPort(55556);
        s.registerChannel(new NettyPacketChannel(0));
        s.registerChannel(new NettyPacketChannel(1));
        s.registerChannel(new NettyPacketChannel(2));
        s.registerChannel(new NettyPacketChannel(3));
        s.registerChannel(new NettyPacketChannel(4));
        s.init();

        new Thread(Test::client).start();
        client();

        System.out.println("Stopping..");
        s.stop();
        System.out.println("Stopped!");
    }

    public static void client() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("packetHandler", new TestClientHandler());
                        }
                    });

            ChannelFuture future = b.connect("localhost", 55556).sync();

            workerGroup.shutdownGracefully().sync();
            future.channel().closeFuture().sync();
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
