package de.bethibande.netty.test;

import de.bethibande.netty.NettyConnection;
import de.bethibande.netty.channels.ChannelListenerAdapter;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.channels.NettyPacketChannel;
import de.bethibande.netty.packets.Packet;
import de.bethibande.netty.packets.ReflectPacketFactory;
import de.bethibande.netty.server.NettyServer;
import de.bethibande.netty.test.packets.AuthPacket;
import de.bethibande.netty.test.packets.MessagePacket;

public class EchoServer {

    public static class AuthListener extends ChannelListenerAdapter {

        private final NettyServer owner;

        public AuthListener(NettyServer owner) {
            this.owner = owner;
        }

        @Override
        public void onPacketReceived(NettyChannel channel, Packet p, NettyConnection connection) {
            if(p instanceof AuthPacket) {
                owner.broadcastPacket(1, new MessagePacket("SERVER", "New client connected: '" + ((AuthPacket) p).getName() + "'!"));
            }
        }
    }

    public static void main(String[] args) {
        NettyServer server = new NettyServer();
        server.setPort(55557);
        server.registerChannel(new NettyPacketChannel(0)); // auth channel
        server.registerChannel(new NettyPacketChannel(1)); // message channel

        server.getPacketManager().registerPacket(0, AuthPacket.class);
        server.getPacketManager().registerPacketFactory(AuthPacket.class, new ReflectPacketFactory<>(AuthPacket.class));

        server.getPacketManager().registerPacket(1, MessagePacket.class);
        server.getPacketManager().registerPacketFactory(MessagePacket.class, new ReflectPacketFactory<>(MessagePacket.class));

        server.registerListener(0, new AuthListener(server));

        server.init();
    }

}
