package de.bethibande.netty.pipeline;

import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class StandardPipelineChannel extends PipelineChannel {

    public StandardPipelineChannel(NettyPipeline owner) {
        super(owner);
    }

    @Override
    public void onDataRead(SocketAddress context, ByteBuf data) throws Exception {
        getOwner().getOwner().getPacketManager().read(getOwner().getOwner().getConnectionManager().getConnectionByAddress((InetSocketAddress) context), data);
    }
}
