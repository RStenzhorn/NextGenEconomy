package de.rjst.nextgeneconomy.exception;

import java.io.Serial;

public class EconomyPlayerNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -2365191787285422717L;

    public EconomyPlayerNotFoundException(String message) {
        super(message);
    }
}
