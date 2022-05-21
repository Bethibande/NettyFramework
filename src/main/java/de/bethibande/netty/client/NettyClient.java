package de.bethibande.netty.client;

import de.bethibande.netty.conection.ConnectionManager;
import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.channels.ChannelListener;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.exceptions.ChannelIdAlreadyInUseException;
import de.bethibande.netty.exceptions.UnknownChannelId;
import de.bethibande.netty.packets.Packet;
import de.bethibande.netty.packets.PacketFuture;
import de.bethibande.netty.packets.PacketManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class NettyClient implements INettyComponent {

    private InetSocketAddress target;

    private Bootstrap b;
    private ChannelFuture future;
    private EventLoopGroup workerGroup;

    private final HashMap<Integer, NettyChannel> channels = new HashMap<>();
    private final HashMap<Integer, Collection<ChannelListener>> listeners = new HashMap<>();

    private PacketManager packetManager = new PacketManager();
    private ConnectionManager connectionManager = new ConnectionManager();

    public NettyClient setAddress(InetSocketAddress address) {
        this.target = address;
        return this;
    }

    @Override
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @Override
    public Collection<ChannelListener> getListenersByChannelId(int id) {
        return listeners.get(id);
    }

    @Override
    public NettyClient registerListener(int channelId, ChannelListener listener) {
        if (!listeners.containsKey(channelId)) listeners.put(channelId, new ArrayList<>());

        listeners.get(channelId).add(listener);
        return this;
    }

    @Override
    public void removeListener(int channelId, ChannelListener listener) {
        listeners.get(channelId).remove(listener);
    }

    @Override
    public void removeListenersByChannelId(int channelId) {
        listeners.remove(channelId);
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
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            for(NettyChannel channel : channels.values()) {
                                ch.pipeline().addLast(channel);
                            }
                        }
                    });

            future = b.connect(this.target).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PacketFuture sendPacket(int channelId, Packet packet) {
        if(!hasChannelId(channelId)) throw new UnknownChannelId("There is no channel with the id '" + channelId + "'!");

        NettyChannel channel = getChannelById(channelId);
        return channel.sendPacket(packet);
    }

    @Override
    public void stop() {
        try {
            workerGroup.shutdownGracefully().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasChannelId(int id) {
        return this.channels.containsKey(id);
    }

    @Override
    public NettyClient registerChannel(NettyChannel channel) {
        if (channels.containsKey(channel.getId())) {
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
}
