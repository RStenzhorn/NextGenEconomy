package de.rjst.nextgeneconomy;

import de.rjst.nextgeneconomy.setting.NgeMessage;
import de.rjst.nextgeneconomy.setting.NgeSetting;
import lombok.Getter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class NextGenEconomy extends JavaPlugin {

    @Getter
    private static NextGenEconomy instance;

    private static ConfigurableApplicationContext applicationContext;

    @Override
    public void onEnable() {
        instance = this;
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        setupConfig();
        applicationContext =
                new SpringApplicationBuilder(NextGenEconomySpringBoot.class)
                        .bannerMode(Banner.Mode.OFF)
                        .properties(loadConfig())
                        .initializers(appContext -> appContext.setClassLoader(getClassLoader()))
                        .build()
                        .run();

    }

    @Override
    public void onDisable() {
        if (applicationContext != null) {
            applicationContext.close();
        }
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

    private Map<String, Object> loadConfig() {
        final YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        yamlFactory.setResources(new FileSystemResource(new File(getDataFolder(), "config.yml")));
        final var properties = yamlFactory.getObject();

        final Map<String, Object> result = new HashMap<>();
        if (properties != null) {
            for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
                final Object key = entry.getKey();
                result.put(key.toString(), entry.getValue());
            }
        }
        return result;
    }

}
