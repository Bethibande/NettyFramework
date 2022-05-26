package de.bethibande.netty.channels;

import de.bethibande.netty.conection.NettyConnection;

public abstract class ChannelListenerAdapter implements ChannelListener {

    @Override
    public void onExceptionCaught(NettyChannel channel, NettyConnection connection, Throwable cause) {
        System.err.println("Exception caught in channel '" + channel.getId() + "'!");
        cause.printStackTrace();
    }
}
