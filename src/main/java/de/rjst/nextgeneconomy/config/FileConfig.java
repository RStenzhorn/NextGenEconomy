package de.rjst.nextgeneconomy.config;


import de.rjst.nextgeneconomy.setting.NgeMessage;
import de.rjst.nextgeneconomy.setting.NgeSetting;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Configuration
public class FileConfig {

    @Bean
    public FileConfiguration fileConfiguration(@NotNull final JavaPlugin plugin) {
        return plugin.getConfig();
    }


    @Bean
    public Map<String, String> propertyMap(@Qualifier("fileConfiguration") final FileConfiguration fileConfiguration) {
        final Map<String, String> result = new HashMap<>();

        String value;
        String path;
        for (final NgeSetting property : NgeSetting.values()) {
            path = property.getPath();
            value = fileConfiguration.getString(path);
            result.put(property.getPath(), value);
        }

        return result;
    }

    @Bean
    public Map<String, String> messageMap(@Qualifier("fileConfiguration") final FileConfiguration fileConfiguration) {
        final Map<String, String> result = new HashMap<>();
        final Locale[] locales = Locale.getAvailableLocales();
        for (final NgeMessage message : NgeMessage.values()) {
            for (final Locale locale : locales) {
                final String path = message.getPath(locale);
                final String value = fileConfiguration.getString(path);
                if (value != null) {
                    result.put(path, value);
                }
            }
        }

        return result;
    }


}
