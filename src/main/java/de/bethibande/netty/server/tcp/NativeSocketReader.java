package de.bethibande.netty.server.tcp;

import de.bethibande.netty.INettyComponent;
import de.bethibande.netty.conection.NettyConnection;
import de.bethibande.netty.pipeline.NettyPipeline;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class NativeSocketReader extends Thread {

    public static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

    private final Socket socket;
    private final INettyComponent owner;
    private final NettyConnection connection;

    private volatile boolean stopping = false;
    private final CompletableFuture<Boolean> stopped = new CompletableFuture<>();

    public NativeSocketReader(ThreadGroup group, Socket socket, INettyComponent owner, NettyConnection connection) {
        super(group, "SocketReader-" + INSTANCE_COUNT.incrementAndGet());

        this.socket = socket;
        this.owner = owner;
        this.connection = connection;

        setPriority(7);
    }

    public Socket getSocket() {
        return socket;
    }

    public INettyComponent getOwner() {
        return owner;
    }

    public NettyConnection getConnection() {
        return connection;
    }

    public void stopService() {
        stopping = true;

        try {
            stopped.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while(!stopping) {
            try {
                int read = socket.getInputStream().read(buffer);
                ByteBuf buf = Unpooled.copiedBuffer(buffer, 0, read);

                NettyPipeline pipeline = owner.getPipeline();
                pipeline.getPipelineChannels().forEach(pipelineChannel -> {
                    try {
                        pipelineChannel.onDataRead(socket.getRemoteSocketAddress(), buf);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch(SocketTimeoutException e) {

            } catch(IOException e) {
                if(e.getMessage() != null && e.getMessage().equalsIgnoreCase("Connection reset")) {
                    stopping = true;
                } else e.printStackTrace();
            }
        }

        try {
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        owner.onDisconnect(owner.getConnectionManager().getConnectionByAddress((InetSocketAddress) socket.getRemoteSocketAddress()));

        stopped.complete(true);
    }
}
