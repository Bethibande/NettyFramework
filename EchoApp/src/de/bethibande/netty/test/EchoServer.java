package de.bethibande.netty.test;

import de.bethibande.netty.ConnectionListener;
import de.bethibande.netty.ConnectionListenerAdapter;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.channels.ChannelListenerAdapter;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.channels.NettyPacketChannel;
import de.bethibande.netty.packets.Packet;
import de.bethibande.netty.packets.PacketManager;
import de.bethibande.netty.packets.ReflectPacketFactory;
import de.bethibande.netty.server.NettyServer;
import de.bethibande.netty.test.packets.AuthPacket;
import de.bethibande.netty.test.packets.InvalidNamePacket;
import de.bethibande.netty.test.packets.MessagePacket;

import java.util.HashMap;

public class EchoServer {

    public static HashMap<NettyConnection, String> names = new HashMap<>();

    public static class ConnectionHandler extends ConnectionListenerAdapter {

        private final NettyServer owner;

        public ConnectionHandler(NettyServer owner) {
            this.owner = owner;
        }

        @Override
        public void onDisconnect(NettyConnection connection) {
            if(!names.containsKey(connection)) return;

            System.out.println("Log > Client disconnected: " + names.get(connection));
            owner.broadcastPacket(1, new MessagePacket("SERVER", "Client disconnected: '" + names.get(connection) + "'!")).complete();
            names.remove(connection);
        }
    }

    public static class AuthListener extends ChannelListenerAdapter {

        private final NettyServer owner;

        public AuthListener(NettyServer owner) {
            this.owner = owner;
        }

        @Override
        public void onExceptionCaught(NettyChannel chanel, NettyConnection connection, Throwable cause) {
            cause.printStackTrace();
        }

        @Override
        public void onPacketReceived(NettyChannel channel, Packet p, NettyConnection connection) {
            if(p instanceof AuthPacket) {
                AuthPacket a = (AuthPacket) p;

                if(names.containsValue(a.getName()) || a.getName().equals("SERVER")) {
                    connection.sendPacket(0, new InvalidNamePacket()).complete();
                    return;
                }

                names.put(connection, a.getName());
                System.out.println("Log > New Client connected: " + a.getName());

                owner.broadcastPacket(1, new MessagePacket("SERVER", "New client connected: '" + a.getName() + "'!")).complete();
            }
        }
    }

    public static class MessageListener extends ChannelListenerAdapter {

        private final NettyServer owner;

        public MessageListener(NettyServer owner) {
            this.owner = owner;
        }

        @Override
        public void onPacketReceived(NettyChannel channel, Packet p, NettyConnection connection) {
            if(p instanceof MessagePacket) {
                MessagePacket m = (MessagePacket) p;

                if(!names.containsKey(connection)) {
                    System.err.println("[Error] Received message from unauthorized client!");
                    return;
                }

                System.out.println("Message > [" + names.get(connection) + "] | " + m.getMessage());

                owner.broadcastPacket(1, new MessagePacket(names.get(connection), m.getMessage())).complete();
            }
        }
    }

    public static void registerPackets(PacketManager manager) {
        manager.registerPacket(0, AuthPacket.class);
        manager.registerPacketFactory(AuthPacket.class, new ReflectPacketFactory<>(AuthPacket.class));

        manager.registerPacket(1, MessagePacket.class);
        manager.registerPacketFactory(MessagePacket.class, new ReflectPacketFactory<>(MessagePacket.class));

        manager.registerPacket(2, InvalidNamePacket.class);
        manager.registerPacketFactory(InvalidNamePacket.class, new ReflectPacketFactory<>(InvalidNamePacket.class));
    }

    public static void main(String[] args) {
        NettyServer server = new NettyServer();
        server.setPort(55557);
        server.registerChannel(new NettyPacketChannel(0)); // auth channel
        server.registerChannel(new NettyPacketChannel(1)); // message channel

        registerPackets(server.getPacketManager());

        server.registerListener(0, new AuthListener(server))
                .registerListener(1, new MessageListener(server))
                .registerConnectionListener(new ConnectionHandler(server));

        server.init();
    }

}
