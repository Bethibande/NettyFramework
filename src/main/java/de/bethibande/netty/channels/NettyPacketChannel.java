package de.bethibande.netty.channels;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.exceptions.UnknownChannelId;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class NettyPacketChannel extends ChannelInboundHandlerAdapter implements NettyChannel {

    private final int id;

    private INettyComponent owner;
    private ChannelHandlerContext context;

    public NettyPacketChannel(int id) {
        this.id = id;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Connected " + ctx.channel().id());
        context = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Disconnected " + ctx.channel().id());
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

        try {
            int packetId = buf.readInt();
            Class<? extends Packet> type = this.owner.getPacketManager().getPacketTypeFromId(packetId);
            Packet packet = this.owner.getPacketManager().newInstanceOfPacket(type);

            long packetLength = buf.readLong();

            packet.read(buf.discardReadBytes());


            ctx.close();
        } finally {
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void setOwner(INettyComponent owner) {
        this.owner = owner;
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
