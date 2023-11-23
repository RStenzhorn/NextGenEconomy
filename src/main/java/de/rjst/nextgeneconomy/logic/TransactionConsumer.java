package de.rjst.nextgeneconomy.logic;

import de.rjst.nextgeneconomy.database.repository.EconomyPlayerRepository;
import de.rjst.nextgeneconomy.database.unit.EconomyPlayerUnit;
import de.rjst.nextgeneconomy.exception.EconomyPlayerNotFoundException;
import de.rjst.nextgeneconomy.exception.NotEnoughCurrencyException;
import de.rjst.nextgeneconomy.model.Transaction;
import de.rjst.nextgeneconomy.model.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service("transactionConsumer")
public class TransactionConsumer implements Consumer<Transaction> {

    private final EconomyPlayerRepository economyPlayerRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public synchronized void accept(final Transaction transaction) {
        final UUID uuid = transaction.getTarget();
        final TransactionType type = transaction.getType();
        final BigDecimal diffCurrency = transaction.getCurrency();

        final EconomyPlayerUnit player = economyPlayerRepository.findById(uuid)
                .orElseThrow(() -> new EconomyPlayerNotFoundException(String.format("UUID: %s not found", uuid)));

        final BigDecimal currentBalance = player.getBalance();
        final BigDecimal result;

        if (type == TransactionType.ADD) {
            result = currentBalance.add(diffCurrency);
        } else if (type == TransactionType.REMOVE){
            if (currentBalance.compareTo(diffCurrency) >= 0) {
                result = currentBalance.subtract(diffCurrency);
            } else {
                throw new NotEnoughCurrencyException(String.format("UUID: %s not found", uuid));
            }
        } else {
            result = diffCurrency;
        }
        player.setBalance(result);
        economyPlayerRepository.save(player);
    }
}
