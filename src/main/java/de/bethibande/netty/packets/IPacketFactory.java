package de.bethibande.netty.packets;

public interface IPacketFactory<T extends INetSerializable> {

    T newPacketInstance();

}
