package de.bethibande.netty.pipeline;

import de.bethibande.netty.INettyComponent;

import java.util.LinkedList;

public class NettyPipeline {

    private final INettyComponent owner;

    private final LinkedList<PipelineChannel> pipelineChannels = new LinkedList<>();

    public NettyPipeline(INettyComponent owner) {
        this.owner = owner;
    }

    public LinkedList<PipelineChannel> getPipelineChannels() {
        return pipelineChannels;
    }

    public void addPipelineChannel(PipelineChannel channel) {
        pipelineChannels.add(channel);
    }

    public void addFirst(PipelineChannel channel) {
        pipelineChannels.addFirst(channel);
    }

    public void removePipelineChannel(PipelineChannel channel) {
        pipelineChannels.remove(channel);
    }

    public INettyComponent getOwner() {
        return owner;
    }
}
