package de.bethibande.netty.conection;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class ConnectionManager {

    private final HashMap<InetSocketAddress, NettyConnection> connections = new HashMap<>();

    public HashMap<InetSocketAddress, NettyConnection> getConnections() {
        return connections;
    }

    public void registerConnection(NettyConnection connection) {
        connections.put(connection.getAddress(), connection);
    }

    public NettyConnection getConnectionByContext(ChannelHandlerContext ctx) {
        for(NettyConnection con : connections.values()) {
            if(con.getContext() == ctx) return con;
        }

        return null;
    }

    public NettyConnection getConnectionByAddress(InetSocketAddress address) {
        return connections.get(address);
    }

    public void unregisterConnection(InetSocketAddress address) {
        connections.remove(address);
    }

    public void unregisterConnection(ChannelHandlerContext ctx) {
        NettyConnection rCon = null;

        for(NettyConnection con : connections.values()) {
            if(con.getContext() == ctx) {
                rCon = con;
                break;
            }
        }

        if(rCon != null) connections.remove(rCon);
    }

}
