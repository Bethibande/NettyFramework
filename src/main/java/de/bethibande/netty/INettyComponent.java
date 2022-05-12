package de.bethibande.netty;

import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.packets.PacketManager;

public interface INettyComponent {

    void init();
    void stop();

    INettyComponent registerChannel(NettyChannel channel);
    NettyChannel getChannelById(int id);
    void deleteChannelById(int id);

    INettyComponent setPacketManager(PacketManager manager);
    PacketManager getPacketManager();

}
