package de.bethibande.netty.channels;

import de.bethibande.netty.NettyConnection;
import de.bethibande.netty.packets.Packet;

public interface ChannelListener {

    void onConnect(NettyConnection connection);

    void onPacketReceived(NettyChannel channel, Packet p, NettyConnection connection);

    void onDisconnect(NettyConnection connection);

}
