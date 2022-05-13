package de.bethibande.netty.channels;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.PacketFuture;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;

import java.util.List;

public interface NettyChannel extends ChannelHandler {

    int getId();

    /**
     * Will be called by NettyServer or NettyClient class once the channel is registered
     * Needed for writing packets inorder to use {@code PacketManager.writePacket(ByteBuf, Packet);} method
     */
    void setOwner(INettyComponent owner);

    void writePacket(ByteBuf buf, INetSerializable packet);

    PacketFuture sendPacket(INetSerializable packet);

}
