package de.rjst.nextgeneconomy.listener.autocomplete;

import de.rjst.nextgeneconomy.config.bean.PluginListener;
import de.rjst.nextgeneconomy.model.TransactionType;
import de.rjst.nextgeneconomy.util.NgeUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@PluginListener
@Component
public class EcoAutoCompleteListener implements Listener {

    private static final String COMMAND = "/eco";

    @EventHandler
    public static void apply(final @NotNull TabCompleteEvent event) {
        final String buffer = event.getBuffer();
        if (buffer.startsWith(COMMAND)) {
            final int argsLength = NgeUtil.getArgsLength(buffer);
            List<String> completions = new ArrayList<>();
            if (argsLength == 3) {
                final String arg = NgeUtil.getArg(buffer, 2);
                completions = NgeUtil.getOfflinePlayers(arg);
            } else if (argsLength == 2) {
                completions = TransactionType.getCompletions();
            }
            event.setCompletions(completions);
        }
    }


}
