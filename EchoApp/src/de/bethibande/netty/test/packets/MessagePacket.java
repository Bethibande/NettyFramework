package de.bethibande.netty.test.packets;

import de.bethibande.netty.packets.Packet;
import de.bethibande.netty.packets.PacketBuffer;

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
    public void read(PacketBuffer buf) {
        this.name = buf.readString();
        this.message = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(this.name);
        buf.writeString(this.message);
    }
}
