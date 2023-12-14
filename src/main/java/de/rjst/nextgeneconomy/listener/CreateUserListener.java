package de.rjst.nextgeneconomy.listener;


import de.rjst.nextgeneconomy.NextGenEconomy;
import de.rjst.nextgeneconomy.api.NextGenEconomyApi;
import de.rjst.nextgeneconomy.config.bean.PluginListener;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.UUID;


@PluginListener
@RequiredArgsConstructor
@Component
public class CreateUserListener implements Listener {

    private final NextGenEconomyApi nextGenEconomyApi;
    private final BukkitScheduler bukkitScheduler;

    @EventHandler
    public void apply(final @NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        bukkitScheduler.runTaskAsynchronously(NextGenEconomy.getInstance(), () -> {
            if (!nextGenEconomyApi.hasAccount(uuid)) {
                nextGenEconomyApi.createAccount(uuid);
            }
        });
    }

}
