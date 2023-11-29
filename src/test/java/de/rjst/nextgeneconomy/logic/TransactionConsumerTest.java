package de.rjst.nextgeneconomy.logic;

import de.rjst.nextgeneconomy.database.repository.EconomyPlayerRepository;
import de.rjst.nextgeneconomy.database.unit.EconomyPlayerUnit;
import de.rjst.nextgeneconomy.exception.EconomyPlayerNotFoundException;
import de.rjst.nextgeneconomy.exception.NotEnoughCurrencyException;
import de.rjst.nextgeneconomy.model.Transaction;
import de.rjst.nextgeneconomy.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TransactionConsumerTest {
    @Mock
    private EconomyPlayerRepository economyPlayerRepository;

    @Mock
    private Transaction transaction;

    @InjectMocks
    private TransactionConsumer underTest;

    @Test
    void testAcceptAddTransaction() {
        final UUID uuid = UUID.randomUUID();
        final BigDecimal initialBalance = BigDecimal.valueOf(100);
        final BigDecimal addAmount = BigDecimal.valueOf(50);

        when(transaction.getTarget()).thenReturn(uuid);
        when(transaction.getType()).thenReturn(TransactionType.ADD);
        when(transaction.getCurrency()).thenReturn(addAmount);

        final EconomyPlayerUnit player = new EconomyPlayerUnit(uuid, initialBalance);
        when(economyPlayerRepository.findById(uuid)).thenReturn(Optional.of(player));

        underTest.accept(transaction);

        assertEquals(initialBalance.add(addAmount), player.getBalance());
        verify(economyPlayerRepository, times(1)).save(player);
    }

    @Test
    void testAcceptRemoveTransactionWithSufficientBalance() {
        final UUID uuid = UUID.randomUUID();
        final BigDecimal initialBalance = BigDecimal.valueOf(100);
        final BigDecimal removeAmount = BigDecimal.valueOf(50);

        when(transaction.getTarget()).thenReturn(uuid);
        when(transaction.getType()).thenReturn(TransactionType.REMOVE);
        when(transaction.getCurrency()).thenReturn(removeAmount);

        final EconomyPlayerUnit player = new EconomyPlayerUnit(uuid, initialBalance);
        when(economyPlayerRepository.findById(uuid)).thenReturn(Optional.of(player));

        underTest.accept(transaction);

        assertEquals(initialBalance.subtract(removeAmount), player.getBalance());
        verify(economyPlayerRepository, times(1)).save(player);
    }

    @Test
    void testAcceptRemoveTransactionWithInsufficientBalance() {
        final UUID uuid = UUID.randomUUID();
        final BigDecimal initialBalance = BigDecimal.valueOf(50);
        final BigDecimal removeAmount = BigDecimal.valueOf(100);

        when(transaction.getTarget()).thenReturn(uuid);
        when(transaction.getType()).thenReturn(TransactionType.REMOVE);
        when(transaction.getCurrency()).thenReturn(removeAmount);

        final EconomyPlayerUnit player = new EconomyPlayerUnit(uuid, initialBalance);
        when(economyPlayerRepository.findById(uuid)).thenReturn(Optional.of(player));

        assertThrows(NotEnoughCurrencyException.class, () -> underTest.accept(transaction));

        // Make sure player balance is not updated
        assertEquals(initialBalance, player.getBalance());
        verify(economyPlayerRepository, never()).save(player);
    }


    @Test
    void testAcceptPlayerNotFound() {
        final UUID uuid = UUID.randomUUID();
        final BigDecimal customAmount = BigDecimal.valueOf(50);

        when(transaction.getTarget()).thenReturn(uuid);
        when(transaction.getType()).thenReturn(TransactionType.ADD);
        when(transaction.getCurrency()).thenReturn(customAmount);

        when(economyPlayerRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(EconomyPlayerNotFoundException.class, () -> underTest.accept(transaction));
        verify(economyPlayerRepository, never()).save(any());
    }
}
