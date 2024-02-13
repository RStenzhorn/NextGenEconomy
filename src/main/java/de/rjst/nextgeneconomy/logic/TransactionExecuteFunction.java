package de.rjst.nextgeneconomy.logic;

import de.rjst.nextgeneconomy.config.exception.EconomyPlayerNotFoundException;
import de.rjst.nextgeneconomy.config.exception.NotEnoughCurrencyException;
import de.rjst.nextgeneconomy.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionExecuteFunction implements Function<Transaction, Boolean> {

    private final Consumer<Transaction> transactionConsumer;

    @Override
    public Boolean apply(final Transaction transaction) {
        boolean result;
        try {
            transactionConsumer.accept(transaction);
            result = true;
        } catch (final NotEnoughCurrencyException | EconomyPlayerNotFoundException ex) {
            log.warn("{} - Transaction: {}", ex.getMessage(), transaction);
            result = false;
        } catch (final RuntimeException ex) {
            log.error("Transaction: {}", transaction, ex);
            result = false;
        }
        return result;
    }
}
