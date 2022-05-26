package de.bethibande.netty.client;

import de.bethibande.netty.ConnectionListener;
import de.bethibande.netty.conection.ConnectionManager;
import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.channels.ChannelListener;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.exceptions.ChannelIdAlreadyInUseException;
import de.bethibande.netty.exceptions.UnknownChannelIdException;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.PacketFuture;
import de.bethibande.netty.packets.PacketManager;
import de.bethibande.netty.pipeline.NettyPipeline;
import de.bethibande.netty.pipeline.PipelineChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.*;

public class NettyClient implements INettyComponent {

    private InetSocketAddress target;

    private Bootstrap b;
    private ChannelFuture future;
    private EventLoopGroup workerGroup;

    private final HashMap<Integer, NettyChannel> channels = new HashMap<>();
    private final HashMap<Integer, Collection<ChannelListener>> listeners = new HashMap<>();
    private final LinkedList<ConnectionListener> connectionListeners = new LinkedList<>();

    private PacketManager packetManager = new PacketManager(this);
    private ConnectionManager connectionManager = new ConnectionManager();
    private NettyPipeline pipeline = new NettyPipeline(this);

    public NettyClient() {
        pipeline.addPipelineChannel(new PipelineChannel(this));
    }

    public NettyClient setAddress(InetSocketAddress address) {
        this.target = address;
        return this;
    }

    @Override
    public INettyComponent registerConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
        return this;
    }

    @Override
    public INettyComponent unregisterConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
        return this;
    }

    @Override
    public NettyPipeline getPipeline() {
        return pipeline;
    }

    @Override
    public void setPipeline(NettyPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void onConnect(ChannelHandlerContext ctx) {
        NettyConnection connection = new NettyConnection((InetSocketAddress) ctx.channel().remoteAddress(), ctx, this);

        getConnectionManager().registerConnection(connection);

        connectionListeners.forEach(listener -> listener.onConnect(connection));
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx) {
        connectionListeners.forEach(listener -> listener.onDisconnect(connectionManager.getConnectionByContext(ctx)));

        getConnectionManager().unregisterConnection((InetSocketAddress) ctx.channel().remoteAddress());
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
                            for(PipelineChannel channel : pipeline.getPipelineChannels()) {
                                ch.pipeline().addLast(channel);
                            }
                        }
                    });

            future = b.connect(this.target).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PacketFuture sendPacket(int channelId, INetSerializable packet) {
        if(!hasChannelId(channelId)) throw new UnknownChannelIdException("There is no channel with the id '" + channelId + "'!");

        Optional<NettyConnection> connectionOptional = connectionManager.getConnections().values().stream().findAny();
        NettyConnection connection = connectionOptional.orElseThrow(() -> new RuntimeException("Sending packet before establishing a connection!"));

        return connection.sendPacket(1, packet);
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
