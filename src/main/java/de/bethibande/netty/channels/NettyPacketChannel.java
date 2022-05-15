package de.bethibande.netty.channels;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.exceptions.PacketChannelException;
import de.bethibande.netty.exceptions.UnknownChannelId;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.Packet;
import de.bethibande.netty.packets.PacketBuffer;
import de.bethibande.netty.packets.PacketFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class NettyPacketChannel extends ChannelInboundHandlerAdapter implements NettyChannel {

    private final int id;

    private INettyComponent owner;
    private ChannelHandlerContext context;
    private NettyConnection connection;

    private ByteBuf buf = null;
    private int length = -1;
    private int packetId = -1;

    public NettyPacketChannel(int id) {
        this.id = id;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
        this.connection = new NettyConnection((InetSocketAddress) ctx.channel().remoteAddress(), context, owner);

        if(owner == null) {
            throw new RuntimeException("Unowned channel connected, channel id '" + id + "'?");
        }

        owner.getConnectionManager().registerConnection(this.connection);
        owner.getListenersByChannelId(id).forEach(channelListener -> channelListener.onConnect(this, connection));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(owner == null) {
            throw new RuntimeException("Unowned channel disconnected, channel id: '" + id + "'?");
        }

        try {
            owner.getListenersByChannelId(id).forEach(channelListener -> channelListener.onDisconnect(this, owner.getConnectionManager().getConnectionByAddress((InetSocketAddress) ctx.channel().remoteAddress())));
        } finally {
            owner.getConnectionManager().unregisterConnection((InetSocketAddress) ctx.channel().remoteAddress());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        int channelId = buf.readInt();

        if (channelId != id) {
            if(ctx.pipeline().last() == this) {
                buf.release();
                throw new UnknownChannelId("There is no channel with the id '" + channelId + "'!");
            }

            buf.resetReaderIndex();
            ctx.fireChannelRead(msg);

            return;
        }

        if(this.context == null) this.context = ctx;

        //String str = (String) buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8);
        //System.out.println("Server > " + id + " | " + channelId + " > " + str + " (" + buf.readableBytes() + ")");

        try {
            while(buf.discardReadBytes().readableBytes() > 0) {
                Packet p = readPacket(buf);

                if(p != null) {
                    owner.getListenersByChannelId(id).forEach(channelListener -> channelListener.onPacketReceived(this, p, owner.getConnectionManager().getConnectionByAddress((InetSocketAddress) ctx.channel().remoteAddress())));
                }
            }
        } finally {
            buf.release();
        }
    }

    public Packet readPacket(ByteBuf buf) {
        if(packetId == -1) packetId = buf.readInt();
        if(length == -1L) length = buf.readInt();

        if(length == buf.readableBytes() || (this.buf != null && length == this.buf.readableBytes())) {
            Class<? extends INetSerializable> type = this.owner.getPacketManager().getPacketTypeById(packetId);

            if(!Packet.class.isAssignableFrom(type)) {
                throw new PacketChannelException("Netty packet channel received packet not assignable from Packet.class!");
            }

            Packet packet = (Packet) this.owner.getPacketManager().newInstanceOfPacket(type);

            if(this.buf == null) this.buf = Unpooled.buffer(length);

            this.buf.writeBytes(buf);
            packet.read(PacketBuffer.wrap(this.buf));

            if(owner == null) {
                throw new RuntimeException("Unowned channel packet received, channel id: '" + id + "'?");
            }

            //System.out.println(id + " " + owner.getListenersByChannelId(id));
            //owner.getListenersByChannelId(id).forEach(channelListener -> channelListener.onPacketReceived(this, packet, owner.getConnectionManager().getConnectionByAddress()));

            packetId = -1;
            length = -1;

            if(this.buf != null) this.buf.release();
            this.buf = null;

            return packet;
        }


        if(this.buf == null) this.buf = Unpooled.buffer(length);
        this.buf.writeBytes(buf);

        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(owner == null) {
            throw new RuntimeException("Unowned channel exception caught, channel id: '" + id + "'?");
        }

        try {
            owner.getListenersByChannelId(id).forEach(channelListener -> channelListener.onExceptionCaught(this, connection, cause));
        } finally {
            ctx.close();
        }
    }

    @Override
    public void setOwner(INettyComponent owner) {
        this.owner = owner;
    }

    @Override
    public PacketFuture sendPacket(INetSerializable packet) {
        ByteBuf buf = Unpooled.buffer();
        writePacket(buf, packet);

        ChannelFuture cf = this.context.pipeline().writeAndFlush(buf);

        return new PacketFuture(cf);
    }

    @Override
    public void writePacket(ByteBuf buf, INetSerializable packet) {
        buf.writeInt(id);

        owner.getPacketManager().writePacket(buf, packet);
    }

    @Override
    public int getId() {
        return id;
    }

}
