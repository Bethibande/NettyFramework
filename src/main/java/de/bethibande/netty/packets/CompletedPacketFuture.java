package de.bethibande.netty.packets;

import java.util.function.Consumer;

public class CompletedPacketFuture extends PacketFuture {

   private final Throwable error;

    public CompletedPacketFuture(Throwable error) {
        super(null);

        this.error = error;
    }

    public CompletedPacketFuture() {
        super(null);

        this.error = null;
    }

    @Override
    public void complete() {
        /* Nothing to see here */
    }

    @Override
    public PacketFuture onSuccess(Runnable runnable) {
        if(error == null) runnable.run();
        return this;
    }

    @Override
    public PacketFuture onFailure(Consumer<Throwable> consumer) {
        if(error != null) consumer.accept(error);
        return this;
    }

    @Override
    public PacketFuture onCancel(Runnable runnable) {
        /* Nothing to see here */
        return this;
    }
}
