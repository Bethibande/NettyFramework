package de.bethibande.netty.packets;

public interface IPacketFactory<T extends Packet> {

    T newPacketInstance();

}
