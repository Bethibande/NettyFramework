package de.bethibande.netty.packets;

import java.util.function.Consumer;

public class SharedPacketFuture extends PacketFuture {

    private final PacketFuture[] futures;

    public SharedPacketFuture(PacketFuture... futures) {
        super(null);
        this.futures = futures;
    }

    @Override
    public void complete() {
        for(PacketFuture f : futures) {
            f.complete();
        }
    }

    public SharedPacketFuture onFailure(Consumer<Throwable> consumer) {
        for(PacketFuture f : futures) {
            f.onFailure(consumer);
        }
        return this;
    }

    public SharedPacketFuture onCancel(Runnable runnable) {
        for(PacketFuture f : futures) {
            f.onCancel(runnable);
        }
        return this;
    }

    public SharedPacketFuture onSuccess(Runnable runnable) {
        for(PacketFuture f : futures) {
            f.onSuccess(runnable);
        }
        return this;
    }



}
