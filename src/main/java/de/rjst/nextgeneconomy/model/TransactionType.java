package de.rjst.nextgeneconomy.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@RequiredArgsConstructor
@Getter
public enum TransactionType {

    ADD("give"),
    SET("set"),
    REMOVE("take");

    private final String completions;

    public static @NotNull List<String> getCompletions () {
        final List<String> result = new ArrayList<>();
        for (final TransactionType value : values()) {
            result.add(value.completions);
        }
        return result;
    }

}
