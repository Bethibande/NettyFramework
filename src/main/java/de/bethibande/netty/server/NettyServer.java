package de.bethibande.netty.server;

import de.bethibande.netty.ConnectionListener;
import de.bethibande.netty.conection.ConnectionManager;
import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.channels.ChannelListener;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.exceptions.ChannelIdAlreadyInUseException;
import de.bethibande.netty.exceptions.UnknownChannelIdException;
import de.bethibande.netty.packets.*;
import de.bethibande.netty.pipeline.NettyPipeline;
import de.bethibande.netty.pipeline.PipelineChannel;
import de.bethibande.netty.pipeline.PipelineChannelWrapper;
import de.bethibande.netty.pipeline.StandardPipelineChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.*;

public class NettyServer implements INettyComponent {

    private ServerBootstrap server;
    private ChannelFuture future;

    private InetSocketAddress address;

    // TODO: setter
    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;

    private PacketManager packetManager = new PacketManager(this);
    private ConnectionManager connectionManager = new ConnectionManager();
    private NettyPipeline pipeline = new NettyPipeline(this);

    private final HashMap<Integer, NettyChannel> channels = new HashMap<>();
    private final HashMap<Integer, Collection<ChannelListener>> listeners = new HashMap<>();
    private final LinkedList<ConnectionListener> connectionListeners = new LinkedList<>();

    public NettyServer() {
        pipeline.addPipelineChannel(new StandardPipelineChannel(pipeline));
    }

    public NettyServer setPort(int port) {
        this.address = new InetSocketAddress("0.0.0.0", port);
        return this;
    }

    public InetSocketAddress getBindAddress() {
        return address;
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
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @Override
    public Collection<ChannelListener> getListenersByChannelId(int id) {
        return listeners.get(id);
    }

    @Override
    public NettyServer registerListener(int channelId, ChannelListener listener) {
        if(!listeners.containsKey(channelId)) listeners.put(channelId, new ArrayList<>());

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
    public NettyServer registerChannel(NettyChannel channel) {
        if(channels.containsKey(channel.getId())) {
            throw new ChannelIdAlreadyInUseException("The channel id '" + channel.getId() + "' is already in use!");
        }

        channels.put(channel.getId(), channel);
        channel.setOwner(this);

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
    public void onConnect(NettyConnection connection) {
        getConnectionManager().registerConnection(connection);

        connectionListeners.forEach(listener -> listener.onConnect(connection));
    }

    @Override
    public void onDisconnect(NettyConnection connection) {
        getConnectionManager().unregisterConnection(connection.getAddress());

        connectionListeners.forEach(listener -> listener.onDisconnect(connection));
    }

    @Override
    public NettyChannel getChannelById(int id) {
        return channels.get(id);
    }

    @Override
    public void deleteChannelById(int id) {
        channels.remove(id);
    }

    @Override
    public boolean hasChannelId(int id) {
        return this.channels.containsKey(id);
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

    public SharedPacketFuture broadcastPacket(int channelId, INetSerializable packet) {
        if(!hasChannelId(channelId)) throw new UnknownChannelIdException("There is no channel with the id '" + channelId + "'!");

        //ByteBuf buf = Unpooled.buffer();
        //buf.writeInt(channelId);

        //packetManager.writePacket(buf, packet);

        List<PacketFuture> futures = new ArrayList<>(); // TODO: improve performance
        for(NettyConnection con : connectionManager.getConnections().values()) {
            //buf.resetReaderIndex();

            //ChannelFuture cf = con.getContext().writeAndFlush(buf);
            //futures.add(new PacketFuture(cf));
            PacketFuture pf = con.sendPacket(channelId, packet);
            futures.add(pf);
        }

        return new SharedPacketFuture(futures.toArray(PacketFuture[]::new));
    }

    public void init() {
        if(server != null) {
            System.err.println("Server already initialized!");
        }

        if(bossEventLoopGroup == null) bossEventLoopGroup = new NioEventLoopGroup();
        if(workerEventLoopGroup == null) workerEventLoopGroup = new NioEventLoopGroup();

        try {
            server = new ServerBootstrap();
            server.group(bossEventLoopGroup, workerEventLoopGroup)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.AUTO_READ, false)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            for(PipelineChannel channel : pipeline.getPipelineChannels()) {
                                ch.pipeline().addLast(new PipelineChannelWrapper(channel));
                            }
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128);

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
