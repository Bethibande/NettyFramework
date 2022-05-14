package de.bethibande.netty.test.packets;

import de.bethibande.netty.packets.Packet;
import io.netty.buffer.ByteBuf;

public class InvalidNamePacket extends Packet {

    @Override
    public void read(ByteBuf buf) {

    }

    @Override
    public void write(ByteBuf buf) {

    }
}
