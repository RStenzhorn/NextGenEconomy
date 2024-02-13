package de.rjst.nextgeneconomy.listener.autocomplete;

import de.rjst.nextgeneconomy.config.bean.PluginListener;
import de.rjst.nextgeneconomy.database.unit.EconomyPlayerUnit;
import de.rjst.nextgeneconomy.util.NgeUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    public final void apply(final @NotNull TabCompleteEvent event) {
        final String buffer = event.getBuffer();
        if (buffer.startsWith(COMMAND)) {
            final Page<EconomyPlayerUnit> pages = balanceTopSupplier.apply(0);
            final int argsLength = NgeUtil.getArgsLength(buffer);

            List<String> completions = new ArrayList<>();
            if (argsLength == 2) {
                final String arg = NgeUtil.getArg(buffer, 1);
                completions = getCompletionsPages(pages, arg);
            }
            event.setCompletions(completions);
        }
    }

    private static @NotNull List<String> getCompletionsPages(final @NotNull Page<EconomyPlayerUnit> pages, final String startWith) {
        final List<String> result = new LinkedList<>();
        final int totalPages = pages.getTotalPages();
        for (int i = 1; i <= totalPages; i++) {
            final String pageString = String.valueOf(i);
            if (pageString.startsWith(startWith)) {
                result.add(pageString);
            }
        }
        return result;
    }

}
