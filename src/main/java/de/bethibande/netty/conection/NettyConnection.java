package de.bethibande.netty.conection;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.PacketFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class NettyConnection {

    private final InetSocketAddress address;
    private final ChannelHandlerContext context;
    private final INettyComponent owner;

    private final HashMap<String, Object> meta = new HashMap<>();

    public NettyConnection(InetSocketAddress address, ChannelHandlerContext context, INettyComponent owner) {
        this.address = address;
        this.context = context;
        this.owner = owner;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public PacketFuture sendPacket(int channelId, INetSerializable packet) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(channelId);

        this.owner.getPacketManager().writePacket(buf, packet);

        ChannelFuture cf = this.context.writeAndFlush(buf);
        return new PacketFuture(cf);
    }

    public HashMap<String, Object> getMeta() {
        return this.meta;
    }

}
