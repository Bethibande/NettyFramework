package de.bethibande.netty.server.tcp;

import de.bethibande.netty.client.NativeClient;
import de.bethibande.netty.conection.NativeNettyConnection;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.server.NativeServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionThread extends Thread {

    public static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger();

    private final NativeServer owner;
    private final ServerSocket socket;

    private volatile boolean stopping = false;

    private final ThreadGroup group = new ThreadGroup("SocketReaders");
    private final List<NativeSocketReader> readers = new ArrayList<>();

    public ConnectionThread(NativeServer owner, ServerSocket socket) {
        super("ConnectionThread-" + INSTANCE_COUNTER.incrementAndGet());

        this.owner = owner;
        this.socket = socket;

        setPriority(8);
    }

    public NativeServer getOwner() {
        return owner;
    }

    public void onDisconnect(InetSocketAddress address) {
        NativeSocketReader remove = null;

        for(NativeSocketReader reader : readers) {
            if(address.equals(reader.getSocket().getRemoteSocketAddress())) remove = reader;
        }

        if(remove != null) readers.remove(remove);
    }

    @Override
    public void run() {
        while(!stopping) {
            try {
                Socket so = socket.accept();
                so.setTrafficClass(NativeClient.TOS_FIELD);
                so.setKeepAlive(true);

                NettyConnection connection = new NativeNettyConnection(owner, so);
                owner.onConnect(connection);

                NativeSocketReader reader = new NativeSocketReader(group, so, owner, connection);
                readers.add(reader);

                reader.start();
            } catch(IOException ex) { }
        }
    }

    public void stopService() {
        stopping = true;

        readers.forEach(NativeSocketReader::stopService);

        interrupt();
    }

}
