package de.bethibande.netty.conection;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.PacketFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

public class StandardNettyConnection implements NettyConnection {

    private final InetSocketAddress address;
    private final ChannelHandlerContext context;
    private final INettyComponent owner;
    private final PacketReader reader = new PacketReader(this);

    private final ConnectionMeta meta = new ConnectionMeta();

    public StandardNettyConnection(InetSocketAddress address, ChannelHandlerContext context, INettyComponent owner) {
        this.address = address;
        this.context = context;
        this.owner = owner;
    }

    public INettyComponent getOwner() {
        return owner;
    }

    public PacketReader getReader() {
        return reader;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public boolean isWritable() {
        return this.context.channel().isWritable();
    }

    public PacketFuture sendPacket(int channelId, INetSerializable packet) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(channelId);

        this.owner.getPacketManager().writePacket(buf, packet);

        ChannelFuture cf = this.context.channel().writeAndFlush(buf);
        return new PacketFuture(cf);
    }

    public ConnectionMeta getMeta() {
        return this.meta;
    }

}
