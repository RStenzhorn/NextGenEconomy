package de.rjst.nextgeneconomy;

import de.rjst.nextgeneconomy.setting.NgeMessage;
import de.rjst.nextgeneconomy.setting.NgeSetting;
import lombok.Getter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Locale;


@SpringBootApplication
public class NextGenEconomy extends JavaPlugin {

    @Getter
    private static NextGenEconomy instance;

    private static AnnotationConfigApplicationContext applicationContext;

    @Override
    public final void onEnable() {
        instance = this;
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        setupConfig();

        applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.scan("de.rjst.nextgeneconomy");
        applicationContext.refresh();

    }

    @Override
    public final void onDisable() {
        applicationContext.close();
    }

    private void setupConfig() {
        final FileConfiguration config = getConfig();
        final FileConfigurationOptions options = config.options();

        options.setHeader(List.of(getName() + " by RStenzhorn"));
        for (final NgeSetting setting : NgeSetting.values()) {
            config.addDefault(setting.getPath(), setting.getDefaultValue());
        }

        setupMessageConfig(config);
        options.copyDefaults(true);
        saveConfig();
    }

    private static void setupMessageConfig(final Configuration config) {
        for (final NgeMessage message : NgeMessage.values()) {
            config.addDefault(message.getPath(Locale.ENGLISH), message.getEnglish());
            config.addDefault(message.getPath(Locale.GERMAN), message.getGerman());
        }
    }
}
