package de.bethibande.netty.pipeline;

import de.bethibande.netty.conection.NettyConnection;
import io.netty.buffer.ByteBuf;

import java.net.SocketAddress;

public abstract class PipelineChannel {

    private final NettyPipeline owner;

    public PipelineChannel(NettyPipeline owner) {
        this.owner = owner;
    }

    public NettyPipeline getOwner() {
        return owner;
    }

    public abstract void onDataRead(SocketAddress context, ByteBuf data) throws Exception;

    public ByteBuf onDataWrite(NettyConnection connection, ByteBuf data) throws Exception {
        return data;
    }

}
