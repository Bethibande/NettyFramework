package de.bethibande.netty.server;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.exceptions.ChannelIdAlreadyInUseException;
import de.bethibande.netty.packets.PacketManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class NettyServer implements INettyComponent {

    private ServerBootstrap server;
    private ChannelFuture future;

    private InetSocketAddress address;

    // TODO: setter
    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;

    private PacketManager packetManager = new PacketManager();

    private final HashMap<Integer, NettyChannel> channels = new HashMap<>();

    public NettyServer setPort(int port) {
        this.address = new InetSocketAddress("0.0.0.0", port);
        return this;
    }

    public NettyServer setBindAddress(String address) {
        this.address = new InetSocketAddress(address, this.address.getPort());
        return this;
    }

    public NettyServer setAddress(InetSocketAddress address) {
        this.address = address;
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
    public NettyServer registerChannel(NettyChannel channel) {
        if(channels.containsKey(channel.getId())) {
            throw new ChannelIdAlreadyInUseException("The channel id '" + channel.getId() + "' is already in use!");
        }

        channels.put(channel.getId(), channel);
        channel.setOwner(this);

        return this;
    }

    @Override
    public NettyChannel getChannelById(int id) {
        return channels.get(id);
    }

    @Override
    public void deleteChannelById(int id) {
        channels.remove(id);
    }

    public void stop() {
        try {
            bossEventLoopGroup.shutdownGracefully().sync();
            workerEventLoopGroup.shutdownGracefully().sync();

            future.channel().closeFuture().sync();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        if(server != null) {
            System.err.println("Server already initialized!");
        }

        bossEventLoopGroup = new NioEventLoopGroup();
        workerEventLoopGroup = new NioEventLoopGroup();

        try {
            server = new ServerBootstrap();
            server.group(bossEventLoopGroup, workerEventLoopGroup)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.AUTO_READ, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            for(NettyChannel channel : channels.values()) {
                                ch.pipeline().addLast(channel);
                            }
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            future = server.bind(this.address)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                    .sync();
        } catch(InterruptedException e) {
            e.printStackTrace();
            System.err.println("Couldn't initialize netty server!");
        }
    }

}
