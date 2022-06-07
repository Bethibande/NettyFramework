package de.bethibande.netty.pipeline;

import de.bethibande.netty.conection.StandardNettyConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class PipelineChannelWrapper extends ChannelInboundHandlerAdapter {

    private final PipelineChannel channel;

    public PipelineChannelWrapper(PipelineChannel channel) {
        this.channel = channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel.getOwner().getOwner().onConnect(new StandardNettyConnection((InetSocketAddress) ctx.channel().remoteAddress(), ctx, channel.getOwner().getOwner()));

        ctx.read();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channel.getOwner().getOwner().onDisconnect(channel.getOwner().getOwner().getConnectionManager().getConnectionByContext(ctx));
    }

    // TODO: implement write

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        channel.onDataRead(ctx.channel().remoteAddress(), (ByteBuf)msg);

        ctx.read();
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
