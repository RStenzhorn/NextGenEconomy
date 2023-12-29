package de.rjst.nextgeneconomy.listener.autocomplete;

import de.rjst.nextgeneconomy.config.bean.PluginListener;
import de.rjst.nextgeneconomy.setting.NgePermission;
import de.rjst.nextgeneconomy.util.NgeUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@PluginListener
@Component
public class BalanceAutoCompleteListener implements Listener {

    private static final String COMMAND = "/balance ";

    @EventHandler
    public static void apply(final @NotNull TabCompleteEvent event) {
        final CommandSender sender = event.getSender();
        if (NgeUtil.isPermitted(sender, NgePermission.CMD_BALANCE_OTHER) && COMMAND.equalsIgnoreCase(event.getBuffer())) {
            event.setCompletions(getOfflinePlayers());
        }
    }

    private static @NotNull List<String> getOfflinePlayers() {
        final List<String> result = new LinkedList<>();
        final OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        for (final OfflinePlayer offlinePlayer : offlinePlayers) {
            result.add(offlinePlayer.getName());
        }
        return result;
    }

}
