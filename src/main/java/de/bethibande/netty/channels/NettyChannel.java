package de.bethibande.netty.channels;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.conection.NettyConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface NettyChannel {

    int getId();

    /**
     * Will be called by NettyServer or NettyClient class once the channel is registered
     * Needed for writing packets inorder to use {@code PacketManager.writePacket(ByteBuf, Packet);} method
     */
    void setOwner(INettyComponent owner);

    void channelRead(NettyConnection con, ByteBuf buf) throws Exception;
    void exceptionCaught(NettyConnection con, Throwable cause) throws Exception;
}
