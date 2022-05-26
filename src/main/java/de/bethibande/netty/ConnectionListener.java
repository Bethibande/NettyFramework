package de.bethibande.netty;

import de.bethibande.netty.conection.NettyConnection;

public interface ConnectionListener {

    void onConnect(NettyConnection connection);

    void onDisconnect(NettyConnection connection);

}
