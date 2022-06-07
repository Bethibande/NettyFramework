package de.bethibande.netty.conection;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.PacketFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.net.Socket;

public class NativeNettyConnection implements NettyConnection {

    private final INettyComponent owner;
    private final Socket socket;

    private final PacketReader reader = new PacketReader(this);
    private final ConnectionMeta meta = new ConnectionMeta();

    public NativeNettyConnection(INettyComponent owner, Socket socket) {
        this.owner = owner;
        this.socket = socket;
    }

    @Override
    public INettyComponent getOwner() {
        return owner;
    }

    @Override
    public PacketReader getReader() {
        return reader;
    }

    @Override
    public InetSocketAddress getAddress() {
        return null;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public synchronized PacketFuture sendPacket(int channelId, INetSerializable packet) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(channelId);

        this.owner.getPacketManager().writePacket(buf, packet);

        // TODO: write data to socket
        //return new PacketFuture(cf); // TODO: completed packet future class
    }

    @Override
    public ConnectionMeta getMeta() {
        return meta;
    }
}
