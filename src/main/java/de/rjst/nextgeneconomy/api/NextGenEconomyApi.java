package de.rjst.nextgeneconomy.api;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface NextGenEconomyApi {

    Boolean hasAccount(UUID uuid);
    Boolean createAccount(UUID uuid);

    Optional<BigDecimal> getBalance(UUID uuid);

    Boolean addBalance(UUID uuid, BigDecimal amount);

    Boolean removeBalance(UUID uuid, BigDecimal amount);

    Boolean setBalance(UUID uuid, BigDecimal amount);

    String format(BigDecimal value);

    String getCurrencySymbol();

    String getCurrencySingular();

    String getCurrencyPlural();

    int getFractionalDigits();
}
