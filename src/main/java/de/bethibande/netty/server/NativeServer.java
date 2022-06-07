package de.bethibande.netty.server;

import de.bethibande.netty.ConnectionListener;
import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.channels.ChannelListener;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.conection.ConnectionManager;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.exceptions.ChannelIdAlreadyInUseException;
import de.bethibande.netty.packets.PacketManager;
import de.bethibande.netty.pipeline.NettyPipeline;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class NativeServer implements INettyComponent {

    private ServerSocket socket = null;
    private InetSocketAddress bindAddress;

    private NettyPipeline pipeline = new NettyPipeline(this);
    private PacketManager packetManager = new PacketManager(this);
    private ConnectionManager connectionManager = new ConnectionManager();

    private final HashMap<Integer, NettyChannel> channels = new HashMap<>();
    private final HashMap<Integer, Collection<ChannelListener>> listeners = new HashMap<>();
    private final LinkedList<ConnectionListener> connectionListeners = new LinkedList<>();

    public NativeServer setBindAddress(InetSocketAddress bindAddress) {
        this.bindAddress = bindAddress;
        return this;
    }

    public InetSocketAddress getBindAddress() {
        return bindAddress;
    }

    @Override
    public void init() {
        try {
            socket = new ServerSocket(bindAddress.getPort(), 100, bindAddress.getAddress());

            socket.setSoTimeout(5000);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAlive() {
        return socket != null && !socket.isClosed();
    }

    @Override
    public void stop() {
        if(isAlive()) throw new RuntimeException("Cannot stop offline service!");

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
    public void onConnect(NettyConnection con) {
        connectionManager.registerConnection(con);
    }

    @Override
    public void onDisconnect(NettyConnection con) {
        connectionManager.unregisterConnection(con.getAddress());
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
