package de.bethibande.netty.channels;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.exceptions.PacketChannelException;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.Packet;
import de.bethibande.netty.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;
import java.util.Collection;

public class NettyPacketChannel implements NettyChannel {

    private final int id;

    private INettyComponent owner;

    public NettyPacketChannel(int id) {
        this.id = id;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        Collection<ChannelListener> listeners = owner.getListenersByChannelId(id);
        if(listeners == null) return;

        int packetId = buf.readInt();
        Class<? extends INetSerializable> packetType = owner.getPacketManager().getPacketTypeById(packetId);

        if(!Packet.class.isAssignableFrom(packetType)) {
            throw new PacketChannelException("Netty packet channel received packet not assignable from Packet.class!");
        }

        Packet packet = (Packet) owner.getPacketManager().newInstanceOfPacket(packetType);

        packet.read(PacketBuffer.wrap(buf.discardReadBytes().resetReaderIndex()));

        listeners.forEach(channelListener -> channelListener.onPacketReceived(this, packet, owner.getConnectionManager().getConnectionByContext(ctx)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(owner == null) {
            throw new RuntimeException("Unowned channel exception caught, channel id: '" + id + "'?");
        }

        try {
            owner.getListenersByChannelId(id).forEach(channelListener -> channelListener.onExceptionCaught(this, owner.getConnectionManager().getConnectionByContext(ctx), cause));
        } finally {
            ctx.close();
        }
    }

    @Override
    public void setOwner(INettyComponent owner) {
        this.owner = owner;
    }

    @Override
    public int getId() {
        return id;
    }

}
