package de.bethibande.netty.packets;

import de.bethibande.netty.reflect.ClassUtil;

public class ReflectPacketFactory<T extends Packet> implements IPacketFactory<T> {

    private final Class<T> type;

    public ReflectPacketFactory(Class<T> type) {
        this.type = type;
    }

    @Override
    public T newPacketInstance() {
        return ClassUtil.createClassInstance(type);
    }
}
