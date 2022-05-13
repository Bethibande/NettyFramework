package de.bethibande.netty.packets;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.function.Consumer;

public class PacketFuture {

    private final ChannelFuture future;

    public PacketFuture(ChannelFuture future) {
        this.future = future;
    }

    public void complete() {
        try {
            future.sync();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PacketFuture onSuccess(Runnable runnable) {
        this.future.addListener((ChannelFutureListener) channelFuture -> {
            if(channelFuture.isSuccess()) runnable.run();
        });
        return this;
    }

    public PacketFuture onFailure(Consumer<Throwable> consumer) {
        this.future.addListener((ChannelFutureListener) channelFuture -> {
            if(channelFuture.isDone() && channelFuture.cause() != null) consumer.accept(channelFuture.cause());
        });
        return this;
    }

    public PacketFuture onCancel(Runnable runnable) {
            this.future.addListener((ChannelFutureListener) channelFuture -> {
                if(channelFuture.isCancelled()) runnable.run();
            });
            return this;
        }


}
