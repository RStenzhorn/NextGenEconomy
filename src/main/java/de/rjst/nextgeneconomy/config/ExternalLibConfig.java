package de.rjst.nextgeneconomy.config;

import de.rjst.nextgeneconomy.api.NextGenEconomyApi;
import de.rjst.nextgeneconomy.logic.VaultConnector;
import lombok.extern.slf4j.Slf4j;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ExternalLibConfig {

    @Bean
    public ApplicationRunner injectVault(final @NotNull Server server, final NextGenEconomyApi nextGenEconomyApi, final JavaPlugin plugin) {
        final PluginManager pluginManager = server.getPluginManager();
        final ServicesManager servicesManager = server.getServicesManager();

        if (pluginManager.getPlugin("Vault") != null) {
            servicesManager.register(Economy.class, new VaultConnector(nextGenEconomyApi), plugin, ServicePriority.Highest);
            log.info("Vault injected");
        } else {
            log.error("Vault not found");
        }
        return args -> {
        };
    }

}
