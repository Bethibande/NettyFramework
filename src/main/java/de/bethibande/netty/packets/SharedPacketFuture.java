package de.bethibande.netty.packets;

import java.util.function.Consumer;

public class SharedPacketFuture {

    private final PacketFuture[] futures;

    public SharedPacketFuture(PacketFuture... futures) {
        this.futures = futures;
    }

    public void complete() {
        for(PacketFuture f : futures) {
            f.complete();
        }
    }

    public void onFailure(Consumer<Throwable> consumer) {
        for(PacketFuture f : futures) {
            f.onFailure(consumer);
        }
    }

    public void onCancel(Runnable runnable) {
        for(PacketFuture f : futures) {
            f.onCancel(runnable);
        }
    }

    public void onSuccess(Runnable runnable) {
        for(PacketFuture f : futures) {
            f.onSuccess(runnable);
        }
    }



}
