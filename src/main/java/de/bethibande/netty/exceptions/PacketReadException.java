package de.bethibande.netty.exceptions;

public class PacketReadException extends RuntimeException {

    public PacketReadException(String message) {
        super(message);
    }
}
