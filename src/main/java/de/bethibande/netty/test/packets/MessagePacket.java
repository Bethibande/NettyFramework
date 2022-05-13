package de.bethibande.netty.test.packets;

import de.bethibande.netty.packets.Packet;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class MessagePacket extends Packet {

    private String name;
    private String message;

    public MessagePacket(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void read(ByteBuf buf) {
        int length = buf.readInt();
        this.name = (String) buf.readCharSequence(length, StandardCharsets.UTF_8);

        length = buf.readInt();
        this.message = (String) buf.readCharSequence(length, StandardCharsets.UTF_8);
    }

    @Override
    public void write(ByteBuf buf) {
        byte[] b = this.name.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(b.length);
        buf.writeBytes(b);

        b = this.message.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(b.length);
        buf.writeBytes(b);
    }
}
