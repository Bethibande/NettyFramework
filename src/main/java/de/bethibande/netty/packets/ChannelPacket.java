package de.bethibande.netty.packets;

/**
 * This class is kind of unnecessary, but the PacketManager.read(ByteBuf) method needs to return both the INetSerializable and the channelId. \(o_o)/
 * PacketManager.read(ByteBuf) may be refactored into a PacketReader class instead in the future.
 */
public class ChannelPacket {

    private final int channelId;
    private final INetSerializable packet;

    public ChannelPacket(int channelId, INetSerializable packet) {
        this.channelId = channelId;
        this.packet = packet;
    }

    public int getChannelId() {
        return channelId;
    }

    public INetSerializable getPacket() {
        return packet;
    }
}
