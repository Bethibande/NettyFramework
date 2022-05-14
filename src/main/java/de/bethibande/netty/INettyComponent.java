package de.bethibande.netty;

import de.bethibande.netty.channels.ChannelListener;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.conection.ConnectionManager;
import de.bethibande.netty.packets.PacketManager;

import java.util.Collection;

public interface INettyComponent {

    void init();
    void stop();

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
