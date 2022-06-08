package de.bethibande.netty.server;

import de.bethibande.netty.ConnectionListener;
import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.channels.ChannelListener;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.client.NativeClient;
import de.bethibande.netty.conection.ConnectionManager;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.exceptions.ChannelIdAlreadyInUseException;
import de.bethibande.netty.exceptions.UnknownChannelIdException;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.PacketFuture;
import de.bethibande.netty.packets.PacketManager;
import de.bethibande.netty.packets.SharedPacketFuture;
import de.bethibande.netty.pipeline.NettyPipeline;
import de.bethibande.netty.pipeline.StandardPipelineChannel;
import de.bethibande.netty.server.tcp.ConnectionThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.*;

public class NativeServer implements INettyComponent {

    private ServerSocket socket = null;
    private InetSocketAddress bindAddress;

    private NettyPipeline pipeline = new NettyPipeline(this);
    private PacketManager packetManager = new PacketManager(this);
    private ConnectionManager connectionManager = new ConnectionManager();

    private final HashMap<Integer, NettyChannel> channels = new HashMap<>();
    private final HashMap<Integer, Collection<ChannelListener>> listeners = new HashMap<>();
    private final LinkedList<ConnectionListener> connectionListeners = new LinkedList<>();

    private ConnectionThread connectionThread;

    public NativeServer() {
        pipeline.addPipelineChannel(new StandardPipelineChannel(pipeline));
    }

    public NativeServer setPort(int port) {
        this.bindAddress = new InetSocketAddress("0.0.0.0", port);
        return this;
    }

    public InetSocketAddress getBindAddress() {
        return bindAddress;
    }

    public NativeServer setBindAddress(String address) {
        this.bindAddress = new InetSocketAddress(address, this.bindAddress.getPort());
        return this;
    }

    public NativeServer setBindAddress(InetSocketAddress address) {
        this.bindAddress = address;
        return this;
    }

    @Override
    public void init() {
        try {
            socket = new ServerSocket(bindAddress.getPort(), 100, bindAddress.getAddress());

            socket.setSoTimeout(NativeClient.SO_TIMEOUT);

            connectionThread = new ConnectionThread(this, socket);
            connectionThread.start();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public SharedPacketFuture broadcastPacket(int channelId, INetSerializable packet) {
        if(!hasChannelId(channelId)) throw new UnknownChannelIdException("There is no channel with the id '" + channelId + "'!");

        List<PacketFuture> futures = new ArrayList<>(); // TODO: improve performance
        for(NettyConnection con : connectionManager.getConnections().values()) {
            PacketFuture pf = con.sendPacket(channelId, packet);
            futures.add(pf);
        }

        return new SharedPacketFuture(futures.toArray(PacketFuture[]::new));
    }

    public boolean isAlive() {
        return socket != null && !socket.isClosed();
    }

    @Override
    public void stop() {
        if(!isAlive()) throw new RuntimeException("Cannot stop offline service!");

        try {
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
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

        connectionThread.onDisconnect(connection.getAddress());
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
