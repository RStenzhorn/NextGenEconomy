package de.rjst.nextgeneconomy.config.async;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaperAsyncExecutor implements AsyncExecutor {

    private final BukkitScheduler bukkitScheduler;
    private final JavaPlugin plugin;

    public void accept(final Runnable runnable) {
        bukkitScheduler.runTaskAsynchronously(plugin, runnable);
    }

}
