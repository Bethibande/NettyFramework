package de.bethibande.netty.conection;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.packets.CompletedPacketFuture;
import de.bethibande.netty.packets.INetSerializable;
import de.bethibande.netty.packets.PacketFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.io.OutputStream;
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

    public Socket getSocket() {
        return socket;
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
        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    private synchronized void writeData(ByteBuf buf) throws IOException {
        OutputStream out = socket.getOutputStream();

        byte[] buffer = new byte[buf.readableBytes()];
        buf.readBytes(buffer);

        out.write(buffer);
        out.flush();

        buf.release();
    }

    @Override
    public PacketFuture sendPacket(int channelId, INetSerializable packet) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(channelId);

        this.owner.getPacketManager().writePacket(buf, packet);

        try {
            writeData(buf);
        } catch(IOException e) {
            e.printStackTrace();
            return new CompletedPacketFuture(e);
        }

        return new CompletedPacketFuture();
    }

    @Override
    public ConnectionMeta getMeta() {
        return meta;
    }
}
