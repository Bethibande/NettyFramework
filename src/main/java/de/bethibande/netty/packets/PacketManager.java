package de.bethibande.netty.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;

public class PacketManager {

    private final HashMap<Integer, Class<? extends Packet>> packetTypes = new HashMap<>();
    private final HashMap<Class<? extends Packet>, IPacketFactory<? extends Packet>> packetFactories = new HashMap<>();

    public void registerPacket(int id, Class<? extends Packet> type) {
        packetTypes.put(id, type);
    }

    public Class<? extends Packet> getPacketTypeFromId(int id) {
        return packetTypes.get(id);
    }

    public <T extends Packet> void registerPacketFactory(Class<T> type, IPacketFactory<T> factory) {
        packetFactories.put(type, factory);
    }

    public <T extends Packet> T newInstanceOfPacket(Class<T> type) {
        return (T) packetFactories.get(type).newPacketInstance();
    }

    public void writePacket(ByteBuf buf, INetSerializable packet) {
        ByteBuf packetBuffer = Unpooled.buffer();
        packet.write(packetBuffer);

        packetBuffer.resetReaderIndex();

        buf.writeInt(packetBuffer.readableBytes());
        buf.writeBytes(packetBuffer);

        packetBuffer.release();
    }

}
