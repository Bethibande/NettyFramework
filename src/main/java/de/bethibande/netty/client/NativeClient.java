package de.bethibande.netty.client;

import de.bethibande.netty.ConnectionListener;
import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.channels.ChannelListener;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.conection.ConnectionManager;
import de.bethibande.netty.conection.NativeNettyConnection;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.exceptions.ChannelIdAlreadyInUseException;
import de.bethibande.netty.exceptions.UnknownChannelIdException;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.PacketFuture;
import de.bethibande.netty.packets.PacketManager;
import de.bethibande.netty.pipeline.NettyPipeline;
import de.bethibande.netty.pipeline.StandardPipelineChannel;
import de.bethibande.netty.server.tcp.NativeSocketReader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

public class NativeClient implements INettyComponent {

    public static final int TOS_FIELD = 0x04 | 0x10;
    public static final int SO_TIMEOUT = 2500;

    private Socket socket = null;
    private InetSocketAddress bindAddress;

    private NettyPipeline pipeline = new NettyPipeline(this);
    private PacketManager packetManager = new PacketManager(this);
    private ConnectionManager connectionManager = new ConnectionManager();

    private final HashMap<Integer, NettyChannel> channels = new HashMap<>();
    private final HashMap<Integer, Collection<ChannelListener>> listeners = new HashMap<>();
    private final LinkedList<ConnectionListener> connectionListeners = new LinkedList<>();

    private NativeSocketReader reader;

    public NativeClient() {
        pipeline.addPipelineChannel(new StandardPipelineChannel(pipeline));
    }

    public NativeClient setBindAddress(InetSocketAddress bindAddress) {
        this.bindAddress = bindAddress;
        return this;
    }

    public InetSocketAddress getBindAddress() {
        return bindAddress;
    }

    @Override
    public void init() {
        try {
            socket = new Socket(bindAddress.getAddress(), bindAddress.getPort());
            socket.setKeepAlive(true);
            socket.setTcpNoDelay(true);

            socket.setSoTimeout(SO_TIMEOUT);

            socket.setTrafficClass(TOS_FIELD);

            NativeNettyConnection connection = new NativeNettyConnection(this, socket);
            onConnect(connection);

            reader = new NativeSocketReader(new ThreadGroup("ReaderGroup"), socket, this, connectionManager.getConnections().values().iterator().next());
            reader.start();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public PacketFuture sendPacket(int channelId, INetSerializable packet) {
        if(!channels.containsKey(channelId)) throw new UnknownChannelIdException("There is no such channel with the id '" + channelId + "'!");

        return connectionManager.getConnections().values().iterator().next().sendPacket(channelId, packet);
    }

    public boolean isAlive() {
        return socket != null && !socket.isClosed();
    }

    @Override
    public void stop() {
        if(!isAlive()) throw new RuntimeException("Cannot stop offline service!");

        reader.stopService();
        socket = null;
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
        connectionListeners.forEach(listener -> listener.onDisconnect(connection));

        getConnectionManager().unregisterConnection(connection.getAddress());
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
    public INettyComponent registerChannel(NettyChannel channel) {
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

    @Override
    public boolean hasChannelId(int id) {
        return channels.containsKey(id);
    }

    @Override
    public INettyComponent setPacketManager(PacketManager manager) {
        packetManager = manager;
        return this;
    }

    @Override
    public PacketManager getPacketManager() {
        return packetManager;
    }

    @Override
    public Collection<ChannelListener> getListenersByChannelId(int id) {
        return listeners.get(id);
    }

    @Override
    public INettyComponent registerListener(int channelId, ChannelListener listener) {
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
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
