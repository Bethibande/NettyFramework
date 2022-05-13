package de.bethibande.netty.channels;

import de.bethibande.netty.NettyConnection;
import de.bethibande.netty.packets.Packet;

public class ChannelListenerAdapter implements ChannelListener {

    @Override
    public void onConnect(NettyChannel channel, NettyConnection connection) {

    }

    @Override
    public void onPacketReceived(NettyChannel channel, Packet p, NettyConnection connection) {

    }

    @Override
    public void onDisconnect(NettyChannel channel, NettyConnection connection) {

    }

    @Override
    public void onExceptionCaught(NettyChannel chanel, NettyConnection connection, Throwable cause) {
        cause.printStackTrace();
    }
}
