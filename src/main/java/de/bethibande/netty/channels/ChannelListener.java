package de.bethibande.netty.channels;

import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.packets.Packet;

public interface ChannelListener {

    void onConnect(NettyChannel channel, NettyConnection connection);

    void onPacketReceived(NettyChannel channel, Packet p, NettyConnection connection);

    void onDisconnect(NettyChannel channel, NettyConnection connection);

    void onExceptionCaught(NettyChannel chanel, NettyConnection connection, Throwable cause);

}
