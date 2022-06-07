package de.bethibande.netty;

import de.bethibande.netty.channels.ChannelListener;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.conection.ConnectionManager;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.packets.PacketManager;
import de.bethibande.netty.pipeline.NettyPipeline;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;

public interface INettyComponent {

    void init();
    void stop();

    NettyPipeline getPipeline();
    void setPipeline(NettyPipeline pipeline);

    void onConnect(NettyConnection ctx);
    void onDisconnect(NettyConnection ctx);

    INettyComponent registerConnectionListener(ConnectionListener listener);
    INettyComponent unregisterConnectionListener(ConnectionListener listener);

    INettyComponent registerChannel(NettyChannel channel);
    NettyChannel getChannelById(int id);
    void deleteChannelById(int id);
    boolean hasChannelId(int id);

    INettyComponent setPacketManager(PacketManager manager);
    PacketManager getPacketManager();

    Collection<ChannelListener> getListenersByChannelId(int id);
    INettyComponent registerListener(int channelId, ChannelListener listener);
    void removeListener(int channelId, ChannelListener listener);
    void removeListenersByChannelId(int channelId);

    ConnectionManager getConnectionManager();

}
