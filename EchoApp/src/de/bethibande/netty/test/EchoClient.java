package de.bethibande.netty.test;

import de.bethibande.netty.ConnectionListenerAdapter;
import de.bethibande.netty.client.NativeClient;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.channels.ChannelListenerAdapter;
import de.bethibande.netty.channels.NettyChannel;
import de.bethibande.netty.channels.NettyPacketChannel;
import de.bethibande.netty.client.NettyClient;
import de.bethibande.netty.packets.Packet;
import de.bethibande.netty.test.packets.AuthPacket;
import de.bethibande.netty.test.packets.InvalidNamePacket;
import de.bethibande.netty.test.packets.MessagePacket;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class EchoClient {

    public static String name;
    public static NativeClient client;

    public static class ConnectionHandler extends ConnectionListenerAdapter {

        @Override
        public void onConnect(NettyConnection connection) {
            connection.sendPacket(0, new AuthPacket(name)).complete();
        }
    }

    public static class AuthListener extends ChannelListenerAdapter {

        @Override
        public void onPacketReceived(NettyChannel channel, Packet p, NettyConnection connection) {
            if(p instanceof InvalidNamePacket) {
                System.err.println("[Error] Couldn't connect to server, invalid name!");
                client.stop();
            }
        }
    }

    public static class MessageListener extends ChannelListenerAdapter {

        @Override
        public void onPacketReceived(NettyChannel channel, Packet p, NettyConnection connection) {
            if(p instanceof MessagePacket) {
                MessagePacket m = (MessagePacket) p;

                if(m.getName().equals("SERVER")) {
                    System.out.println("Server > " + m.getMessage());
                    return;
                }

                System.out.println("Message > [" + m.getName() + "] | " + m.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        client = new NativeClient();
        client.setBindAddress(new InetSocketAddress("127.0.0.1", 55557));

        client.registerChannel(new NettyPacketChannel(0));
        client.registerChannel(new NettyPacketChannel(1));

        EchoServer.registerPackets(client.getPacketManager());

        client.registerListener(0, new AuthListener())
                .registerListener(1, new MessageListener())
                .registerConnectionListener(new ConnectionHandler());


        // input name
        System.out.println("Input name");
        Scanner s = new Scanner(System.in);
        name = s.nextLine();

        client.init();

        new Thread(() -> {
            while(true) {
                String msg = s.nextLine();
                if(msg == null) continue;

                if(msg.equalsIgnoreCase("QUIT")) {
                    System.out.println("Good Bye!");
                    client.stop();
                    break;
                }

                client.sendPacket(1, new MessagePacket(null, msg));
            }
        }).start();
    }

}
