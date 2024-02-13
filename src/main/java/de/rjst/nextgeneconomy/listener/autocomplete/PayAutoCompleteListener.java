package de.rjst.nextgeneconomy.listener.autocomplete;

import de.rjst.nextgeneconomy.config.bean.PluginListener;
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
public class PayAutoCompleteListener implements Listener {

    private static final String COMMAND = "/pay";

    @EventHandler
    public static void apply(final @NotNull TabCompleteEvent event) {
        final String buffer = event.getBuffer();
        if (buffer.startsWith(COMMAND)) {
            final int argsLength = NgeUtil.getArgsLength(buffer);
            List<String> completions = new ArrayList<>();
            if (argsLength == 2) {
                final String arg = NgeUtil.getArg(buffer, 1);
                completions = NgeUtil.getOfflinePlayers(arg);
            }
            event.setCompletions(completions);
        }
    }


}
