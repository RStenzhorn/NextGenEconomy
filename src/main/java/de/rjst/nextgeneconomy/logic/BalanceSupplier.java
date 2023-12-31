package de.rjst.nextgeneconomy.logic;

import de.rjst.nextgeneconomy.database.repository.EconomyPlayerRepository;
import de.rjst.nextgeneconomy.database.unit.EconomyPlayerUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static de.rjst.nextgeneconomy.setting.NgeCaches.BALANCE;

@RequiredArgsConstructor
@Service("balanceSupplier")
public class BalanceSupplier implements Function<UUID, Optional<BigDecimal>> {

    private final EconomyPlayerRepository economyPlayerRepository;

    @Cacheable(value = BALANCE, key = "#uuid")
    @Override
    public Optional<BigDecimal> apply(final UUID uuid) {
        BigDecimal result = null;

        final Optional<EconomyPlayerUnit> unitOptional = economyPlayerRepository.findById(uuid);
        if (unitOptional.isPresent()) {
            final EconomyPlayerUnit unit = unitOptional.get();
            result = unit.getBalance();
        }

        return Optional.ofNullable(result);
    }
}
