package de.bethibande.netty.packets;

import io.netty.buffer.ByteBuf;

public interface INetSerializable {

    void read(PacketBuffer buf);

    void write(PacketBuffer buf);

}
