package de.rjst.nextgeneconomy.listener;


import de.rjst.nextgeneconomy.api.NextGenEconomyApi;
import de.rjst.nextgeneconomy.config.async.AsyncExecutor;
import de.rjst.nextgeneconomy.config.bean.PluginListener;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.UUID;


@PluginListener
@RequiredArgsConstructor
@Component
public class CreateUserListener implements Listener {

    private final NextGenEconomyApi nextGenEconomyApi;
    private final AsyncExecutor asyncExecutor;

    @EventHandler
    public void apply(final @NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        asyncExecutor.accept(() -> {
            if (!nextGenEconomyApi.hasAccount(uuid)) {
                nextGenEconomyApi.createAccount(uuid);
            }
        });
    }

}
