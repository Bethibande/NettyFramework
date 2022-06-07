package de.bethibande.netty.conection;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.PacketFuture;

import java.net.InetSocketAddress;

public interface NettyConnection {

    public INettyComponent getOwner();

    PacketReader getReader();

    InetSocketAddress getAddress();

    boolean isWritable();

    PacketFuture sendPacket(int channelId, INetSerializable packet);

    ConnectionMeta getMeta();

}
