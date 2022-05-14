package de.bethibande.netty.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;

public class PacketManager {

    private final HashMap<Integer, Class<? extends INetSerializable>> packetTypes = new HashMap<>();
    private final HashMap<Class<? extends INetSerializable>, IPacketFactory<? extends INetSerializable>> packetFactories = new HashMap<>();

    public void registerPacket(int id, Class<? extends INetSerializable> type) {
        packetTypes.put(id, type);
    }

    public Class<? extends INetSerializable> getPacketTypeById(int id) {
        return packetTypes.get(id);
    }

    public int getPacketIdByType(Class<? extends INetSerializable> type) {
        for(int id : packetTypes.keySet()) {
            Class<? extends INetSerializable> t = packetTypes.get(id);
            if(t == type) return id;
        }
        return -1;
    }

    public <T extends INetSerializable> void registerPacketFactory(Class<T> type, IPacketFactory<T> factory) {
        packetFactories.put(type, factory);
    }

    public <T extends INetSerializable> T newInstanceOfPacket(Class<T> type) {
        return (T) packetFactories.get(type).newPacketInstance();
    }

    public void writePacket(ByteBuf buf, INetSerializable packet) {
        ByteBuf packetBuffer = Unpooled.buffer();
        packet.write(packetBuffer);

        packetBuffer.resetReaderIndex();

        buf.writeInt(getPacketIdByType(packet.getClass()));
        buf.writeInt(packetBuffer.readableBytes());
        buf.writeBytes(packetBuffer);

        packetBuffer.release();
    }

}
