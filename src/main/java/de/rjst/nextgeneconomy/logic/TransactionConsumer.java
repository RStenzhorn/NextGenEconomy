package de.rjst.nextgeneconomy.logic;

import de.rjst.nextgeneconomy.database.repository.EconomyPlayerRepository;
import de.rjst.nextgeneconomy.database.unit.EconomyPlayerUnit;
import de.rjst.nextgeneconomy.exception.EconomyPlayerNotFoundException;
import de.rjst.nextgeneconomy.exception.NotEnoughCurrencyException;
import de.rjst.nextgeneconomy.model.Transaction;
import de.rjst.nextgeneconomy.model.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Service("transactionConsumer")
public class TransactionConsumer implements Consumer<Transaction> {

    private final EconomyPlayerRepository economyPlayerRepository;

    @Override
    @CacheEvict(value = "balance", key = "#transaction.target")
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 10)
    public void accept(final @NotNull Transaction transaction) {
        final UUID uuid = transaction.getTarget();
        final TransactionType type = transaction.getType();
        final BigDecimal diffCurrency = transaction.getCurrency().setScale(2, RoundingMode.FLOOR);

        final Optional<EconomyPlayerUnit> playerUnitOptional = economyPlayerRepository.findById(uuid);
        if (playerUnitOptional.isPresent()) {
            final EconomyPlayerUnit player = playerUnitOptional.get();
            final BigDecimal currentBalance = player.getBalance();
            BigDecimal result = null;

            if (type == TransactionType.ADD) {
                result = currentBalance.add(diffCurrency);
            } else if (type == TransactionType.REMOVE) {
                if (currentBalance.compareTo(diffCurrency) >= 0) {
                    result = currentBalance.subtract(diffCurrency);
                } else {
                    throw new NotEnoughCurrencyException(String.format("UUID: %s not enough currency", uuid));
                }
            } else if (type == TransactionType.SET) {
                result = diffCurrency;
            }

            player.setBalance(result);
            economyPlayerRepository.saveAndFlush(player);
        } else {
            throw new EconomyPlayerNotFoundException(String.format("UUID: %s not found", uuid));
        }

    }
}
