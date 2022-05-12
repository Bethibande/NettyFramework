package de.bethibande.netty.exceptions;

public class ChannelIdAlreadyInUseException extends RuntimeException {

    public ChannelIdAlreadyInUseException(String message) {
        super(message);
    }
}
