package de.bethibande.netty.server.tcp;

import de.bethibande.netty.server.NativeServer;

import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionThread extends Thread {

    public static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger();

    private final NativeServer owner;
    private final ServerSocket socket;

    public ConnectionThread(NativeServer owner, ServerSocket socket) {
        super("ConnectionThread-" + INSTANCE_COUNTER.incrementAndGet());

        this.owner = owner;
        this.socket = socket;

        setDaemon(true);
        setPriority(8);
    }

    public NativeServer getOwner() {
        return owner;
    }

    @Override
    public void run() {
        while()
    }
}
