package de.rjst.nextgeneconomy.exception;

import java.io.Serial;

public class NotEnoughCurrencyException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8440201233939614013L;

    public NotEnoughCurrencyException(String message) {
        super(message);
    }
}
