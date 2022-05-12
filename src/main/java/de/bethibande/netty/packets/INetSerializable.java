package de.bethibande.netty.packets;

import io.netty.buffer.ByteBuf;

public interface INetSerializable {

    void read(ByteBuf buf);

    void write(ByteBuf buf);

}
