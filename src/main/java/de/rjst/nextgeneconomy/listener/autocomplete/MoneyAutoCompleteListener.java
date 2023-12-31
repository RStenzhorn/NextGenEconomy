package de.rjst.nextgeneconomy.listener.autocomplete;

import de.rjst.nextgeneconomy.config.bean.PluginListener;
import de.rjst.nextgeneconomy.setting.NgePermission;
import de.rjst.nextgeneconomy.util.NgeUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@PluginListener
@Component
public class MoneyAutoCompleteListener implements Listener {

    private static final String COMMAND = "/money ";

    @EventHandler
    public static void apply(final @NotNull TabCompleteEvent event) {
        final CommandSender sender = event.getSender();
        if (NgeUtil.isPermitted(sender, NgePermission.CMD_BALANCE_OTHER) && COMMAND.equalsIgnoreCase(event.getBuffer())) {
            event.setCompletions(NgeUtil.getOfflinePlayers());
        }
    }

}
