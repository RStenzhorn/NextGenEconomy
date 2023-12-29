package de.rjst.nextgeneconomy.listener.autocomplete;

import de.rjst.nextgeneconomy.config.bean.PluginListener;
import de.rjst.nextgeneconomy.database.unit.EconomyPlayerUnit;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@PluginListener
@Component
public class BalanceTopAutoCompleteListener implements Listener {

    private static final String COMMAND = "/balancetop ";

    @Qualifier("balanceTopSupplier")
    private final Function<Integer, Page<EconomyPlayerUnit>> balanceTopSupplier;

    @EventHandler
    public  void apply(final @NotNull TabCompleteEvent event) {
        if (COMMAND.equalsIgnoreCase(event.getBuffer())) {
            final Page<EconomyPlayerUnit> pages = balanceTopSupplier.apply(0);
            event.setCompletions(getCompletionsPages(pages));
        }
    }

    private static @NotNull List<String> getCompletionsPages(final @NotNull Page<EconomyPlayerUnit> pages) {
        final List<String> result = new LinkedList<>();
        final int totalPages = pages.getTotalPages();
        for (int i = 1; i <= totalPages; i++) {
            result.add(String.valueOf(i));
        }
        return result;
    }

}
