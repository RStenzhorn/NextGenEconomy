package de.rjst.nextgeneconomy.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public enum TransactionType {

    ADD,
    SET,
    REMOVE;

    public static @NotNull List<String> getCompletions () {
        final List<String> result = new ArrayList<>();
        for (final TransactionType value : values()) {
            result.add(value.name().toLowerCase(Locale.ROOT));
        }
        return result;
    }

}
