package de.bethibande.netty.pipeline;

import de.bethibande.netty.INettyComponent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class PipelineChannel extends ChannelInboundHandlerAdapter {

    private final NettyPipeline owner;

    public PipelineChannel(INettyComponent owner) {
        this.owner = owner.getPipeline();
    }

    public PipelineChannel(NettyPipeline owner) {
        this.owner = owner;
    }

    public NettyPipeline getOwner() {
        return owner;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        owner.getOwner().onConnect(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        owner.getOwner().onDisconnect(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        owner.getOwner().getPacketManager().read(ctx, (ByteBuf) msg);
        //ChannelPacket cp = owner.getOwner().getPacketManager().read((ByteBuf) msg);

        //if(cp != null) owner.getOwner().onPacketReceived(cp.getChannelId(), cp.getPacket());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
