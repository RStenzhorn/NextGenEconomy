package de.rjst.nextgeneconomy.logic.command;

import de.rjst.nextgeneconomy.database.repository.EconomyPlayerRepository;
import de.rjst.nextgeneconomy.database.unit.EconomyPlayerUnit;
import de.rjst.nextgeneconomy.logic.config.PropertySupplier;
import de.rjst.nextgeneconomy.setting.NgeSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@RequiredArgsConstructor
@Service("balanceTopSupplier")
public class BalanceTopSupplier implements Function<Integer, Page<EconomyPlayerUnit>> {

    private final EconomyPlayerRepository economyPlayerRepository;

    private static final String BALANCE = "balance";
    private static final String UUID = "id";

    private final PropertySupplier propertySupplier;

    @Cacheable(value = "balanceTop", key = "#page")
    @Override
    public Page<EconomyPlayerUnit> apply(final Integer page) {
        final int size = propertySupplier.apply(NgeSetting.BALANCE_TOP_PAGE_SIZE, Integer.class);
        final Sort sort = Sort.by(Sort.Order.desc(BALANCE), Sort.Order.asc(UUID));
        return economyPlayerRepository.findAll(PageRequest.of(page, size, sort));
    }
}
