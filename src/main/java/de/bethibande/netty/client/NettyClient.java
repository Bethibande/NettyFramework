package de.bethibande.netty.client;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.Test;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.packets.PacketManager;
import de.bethibande.netty.server.NettyServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class NettyClient implements INettyComponent {

    private InetSocketAddress target;

    private Bootstrap b;
    private ChannelFuture future;
    private EventLoopGroup workerGroup;

    private final HashMap<Integer, NettyChannel> channels = new HashMap<>();

    private PacketManager packetManager = new PacketManager();

    public NettyClient setAddress(InetSocketAddress address) {
        this.target = address;
        return this;
    }

    @Override
    public INettyComponent setPacketManager(PacketManager manager) {
        this.packetManager = manager;
        return this;
    }

    @Override
    public PacketManager getPacketManager() {
        return packetManager;
    }

    @Override
    public void init() {
        try {
            workerGroup = new NioEventLoopGroup();

            b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("packetHandler", new Test.TestClientHandler());
                        }
                    });

            future = b.connect(this.target).sync();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            workerGroup.shutdownGracefully().sync();
            future.channel().closeFuture().sync();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public INettyComponent registerChannel(NettyChannel channel) {
        return null;
    }

    @Override
    public NettyChannel getChannelById(int id) {
        return null;
    }

    @Override
    public void deleteChannelById(int id) {

    }
}
