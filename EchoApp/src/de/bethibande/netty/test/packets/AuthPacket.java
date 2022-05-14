package de.bethibande.netty.test.packets;

import de.bethibande.netty.packets.Packet;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class AuthPacket extends Packet {

    private String name;

    public AuthPacket(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void read(ByteBuf buf) {
        int length = buf.readInt();
        name = (String) buf.readCharSequence(length, StandardCharsets.UTF_8);
    }

    @Override
    public void write(ByteBuf buf) {
        byte[] b = this.name.getBytes(StandardCharsets.UTF_8);

        buf.writeInt(b.length);
        buf.writeBytes(b);
    }
}
