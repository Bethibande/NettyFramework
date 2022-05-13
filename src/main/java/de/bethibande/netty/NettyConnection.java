package de.bethibande.netty;

import io.netty.channel.ChannelHandlerContext;

public class NettyConnection {

    private final ChannelHandlerContext context;

    public NettyConnection(ChannelHandlerContext context) {
        this.context = context;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }
}
