package de.bethibande.netty.conection;

import de.bethibande.netty.exceptions.UnknownChannelIdException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class PacketReader {

    private final NettyConnection owner;

    private volatile Integer channelId = null;
    private volatile Integer length = null;
    private volatile ByteBuf read = null;

    public PacketReader(NettyConnection owner) {
        this.owner = owner;
    }

    public NettyConnection getOwner() {
        return owner;
    }

    public synchronized void read(ByteBuf buf) throws Exception {
        while(buf.refCnt() > 0 && buf.readableBytes() > 0) {
            if (channelId == null) {
                channelId = buf.readInt();
                length = buf.readInt();
            }

            if (length == null) return;

            if (read == null) {
                if (length == buf.readableBytes()) read = buf.discardReadBytes();
                if (length > buf.readableBytes()) {
                    read = Unpooled.buffer();
                    read.writeBytes(buf);
                }
                if (length < buf.readableBytes()) {
                    read = Unpooled.buffer();
                    read.writeBytes(buf, length);
                }
            }

            if (read != null) {
                if (length - read.readableBytes() > buf.readableBytes()) read = read.writeBytes(buf);
                if (length - read.readableBytes() <= buf.readableBytes()) read = read.writeBytes(buf, length - read.readableBytes());
            }

            if (read == null) return;

            if (read.readableBytes() >= length) {

                if (!owner.getOwner().hasChannelId(channelId))
                    throw new UnknownChannelIdException("There is no such channel with the id '" + channelId + "'!");

                try {
                    owner.getOwner().getChannelById(channelId).channelRead(owner, read);
                } catch(Exception e) {
                    owner.getOwner().getChannelById(channelId).exceptionCaught(owner, e);
                } finally {
                    read.release();

                    read = null;
                    length = null;
                    channelId = null;
                }
            }
        }

        if(buf.refCnt() > 0) {
            buf.release();
        }
    }

}
