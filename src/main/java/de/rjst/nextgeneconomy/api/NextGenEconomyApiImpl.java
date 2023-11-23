package de.rjst.nextgeneconomy.api;

import de.rjst.nextgeneconomy.database.repository.EconomyPlayerRepository;
import de.rjst.nextgeneconomy.database.unit.EconomyPlayerUnit;
import de.rjst.nextgeneconomy.logic.config.PropertySupplier;
import de.rjst.nextgeneconomy.model.Transaction;
import de.rjst.nextgeneconomy.model.TransactionImpl;
import de.rjst.nextgeneconomy.model.TransactionType;
import de.rjst.nextgeneconomy.setting.NgeSetting;
import de.rjst.nextgeneconomy.setting.Placeholder;
import de.rjst.nextgeneconomy.util.NgeMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;


@Slf4j
@RequiredArgsConstructor
@Service
public class NextGenEconomyApiImpl implements NextGenEconomyApi {

    private final EconomyPlayerRepository economyPlayerRepository;
    private final Consumer<Transaction> transactionConsumer;

    private final PropertySupplier propertySupplier;

    @Override
    public Boolean hasAccount(final UUID uuid) {
        return economyPlayerRepository.findById(uuid).isPresent();
    }

    @Override
    public Boolean createAccount(final UUID uuid) {
        boolean result = false;
        final Optional<EconomyPlayerUnit> unitOptional = economyPlayerRepository.findById(uuid);
        if (unitOptional.isEmpty()) {
            final EconomyPlayerUnit unit = new EconomyPlayerUnit();
            unit.setId(uuid);
            unit.setBalance(propertySupplier.apply(NgeSetting.START_BALANCE, BigDecimal.class));
            economyPlayerRepository.save(unit);
            result = true;
        }
        return result;
    }

    @Override
    public Optional<BigDecimal> getBalance(final UUID uuid) {
        BigDecimal result = null;
        final Optional<EconomyPlayerUnit> unitOptional = economyPlayerRepository.findById(uuid);
        if (unitOptional.isPresent()) {
            final EconomyPlayerUnit unit = unitOptional.get();
            result = unit.getBalance();
        }

        return Optional.ofNullable(result);
    }

    @Override
    public Boolean addBalance(final UUID uuid, final BigDecimal amount) {
        boolean result;
        final Transaction transaction = TransactionImpl.builder()
                .type(TransactionType.ADD)
                .target(uuid)
                .currency(amount)
                .build();
        try {
            transactionConsumer.accept(transaction);
            result = true;
        } catch (final RuntimeException ex) {
            log.warn("Transaction {} failed: {}", transaction, ex.getMessage());
            result = false;
        }
        return result;
    }

    @Override
    public Boolean removeBalance(final UUID uuid, final BigDecimal amount) {
        boolean result;

        final Transaction transaction = TransactionImpl.builder()
                .type(TransactionType.REMOVE)
                .target(uuid)
                .currency(amount)
                .build();
        try {
            transactionConsumer.accept(transaction);
            result = true;
        } catch (final RuntimeException ex) {
            log.warn("Transaction {} failed: {}", transaction, ex.getMessage());
            result = false;
        }
        return result;
    }

    @Override
    public Boolean setBalance(final UUID uuid, final BigDecimal amount) {
        boolean result;
        final Transaction transaction = TransactionImpl.builder()
                .type(TransactionType.SET)
                .target(uuid)
                .currency(amount)
                .build();
        try {
            transactionConsumer.accept(transaction);
            result = true;
        } catch (final RuntimeException ex) {
            log.warn("Transaction {} failed: {}", transaction, ex.getMessage());
            result = false;
        }
        return result;
    }

    @Override
    public String format(final BigDecimal value) {
        final String baseMessage = propertySupplier.apply(NgeSetting.CURRENCY_FORMAT, String.class);
        return NgeMessageUtil.getMessage(baseMessage, Map.of(
                Placeholder.LOCALE_FORMAT, getFormattedDecimal(value),
                Placeholder.CURRENCY_NAME, value.compareTo(BigDecimal.ONE) == 0 ? getCurrencySingular() : getCurrencyPlural(),
                Placeholder.CURRENCY_SYMBOL, getCurrencySymbol()
        ));
    }

    @Override
    public String getCurrencySymbol() {
        return propertySupplier.apply(NgeSetting.CURRENCY_SYMBOL, String.class);
    }

    @Override
    public String getCurrencySingular() {
        return propertySupplier.apply(NgeSetting.CURRENCY_SINGULAR, String.class);
    }

    @Override
    public String getCurrencyPlural() {
        return propertySupplier.apply(NgeSetting.CURRENCY_PLURAL, String.class);
    }

    @Override
    public int getFractionalDigits() {
        return propertySupplier.apply(NgeSetting.CURRENCY_FRACTION_DIGITS, Integer.class);
    }

    private final @NotNull String getFormattedDecimal(final BigDecimal currency) {
        final String localeFormat = propertySupplier.apply(NgeSetting.CURRENCY_LOCALE_FORMAT, String.class);
        final String[] localeParts = localeFormat.split("_");
        if (localeParts.length != 2) {
            throw new IllegalArgumentException("Invalid Currency Locale");
        }
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale(localeParts[0], localeParts[1]));
        numberFormat.setMinimumFractionDigits(getFractionalDigits());
        numberFormat.setMaximumFractionDigits(getFractionalDigits());
        return numberFormat.format(currency);
    }
}
