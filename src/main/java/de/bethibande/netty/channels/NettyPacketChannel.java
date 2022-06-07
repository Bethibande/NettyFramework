package de.bethibande.netty.channels;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.exceptions.PacketChannelException;
import de.bethibande.netty.exceptions.PacketReadException;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.Packet;
import de.bethibande.netty.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;

public class NettyPacketChannel implements NettyChannel {

    private final int id;

    private INettyComponent owner;

    public NettyPacketChannel(int id) {
        this.id = id;
    }

    @Override
    public void channelRead(NettyConnection con, ByteBuf buf) throws Exception {
        Collection<ChannelListener> listeners = owner.getListenersByChannelId(id);
        if(listeners == null) return;

        int packetId = buf.readInt();
        Class<? extends INetSerializable> packetType = owner.getPacketManager().getPacketTypeById(packetId);
        if(packetType == null) throw new PacketReadException("Unable to read packet, unknown packet id: '" + packetId + "' in channel '" + id + "'");

        if(!Packet.class.isAssignableFrom(packetType)) {
            throw new PacketChannelException("Netty packet channel received packet not assignable from Packet.class!");
        }

        Packet packet = (Packet) owner.getPacketManager().newInstanceOfPacket(packetType);

        packet.read(PacketBuffer.wrap(buf.discardReadBytes().resetReaderIndex()));

        listeners.forEach(channelListener -> channelListener.onPacketReceived(this, packet, con));
    }

    @Override
    public void exceptionCaught(NettyConnection con, Throwable cause) throws Exception {
        if(owner == null) {
            throw new RuntimeException("Unowned channel exception caught, channel id: '" + id + "'?");
        }

        Collection<ChannelListener> listeners = owner.getListenersByChannelId(id);
        if (listeners == null || listeners.isEmpty()) {
            System.err.println("Exception thrown in Channel without channel listeners!");
            cause.printStackTrace();
            return;
        }

        listeners.forEach(channelListener -> channelListener.onExceptionCaught(this, con, cause));
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
