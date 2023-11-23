package de.rjst.nextgeneconomy.model;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public interface Transaction {

    @NotNull
    UUID getTarget();

    @NotNull
    TransactionType getType();

    @NotNull
    BigDecimal getCurrency();

}
