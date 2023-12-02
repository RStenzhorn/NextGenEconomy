package de.rjst.nextgeneconomy.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@UtilityClass
public class NgeMessageUtil {

    public String getMessage(final String msg, final @NotNull Map<String, String> placeholders) {
        var result = msg;
        for (final Map.Entry<String, String> entry : placeholders.entrySet()) {
            final String placeHolderKey = getPlaceholderKey(entry.getKey());
            final String value = entry.getValue();
            result = result.replace(placeHolderKey, value);
        }
        return result;
    }

    public @NotNull Component getComponent(final String message) {
        final var replacedColor = replaceLegacyColors(message);
        return MiniMessage.miniMessage().deserialize(replacedColor);
    }

    private final Map<String, String> COLOR_MAP;

    static {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put("&0", "<black>");
        COLOR_MAP.put("&1", "<dark_blue>");
        COLOR_MAP.put("&2", "<dark_green>");
        COLOR_MAP.put("&3", "<dark_aqua>");
        COLOR_MAP.put("&4", "<dark_red>");
        COLOR_MAP.put("&5", "<dark_purple>");
        COLOR_MAP.put("&6", "<gold>");
        COLOR_MAP.put("&7", "<gray>");
        COLOR_MAP.put("&8", "<dark_gray>");
        COLOR_MAP.put("&9", "<blue>");
        COLOR_MAP.put("&a", "<green>");
        COLOR_MAP.put("&b", "<aqua>");
        COLOR_MAP.put("&c", "<red>");
        COLOR_MAP.put("&d", "<light_purple>");
        COLOR_MAP.put("&e", "<yellow>");
        COLOR_MAP.put("&f", "<white>");

        COLOR_MAP.put("&r", "<reset>");
        COLOR_MAP.put("&l", "<bold>");
        COLOR_MAP.put("&o", "<italic>");
        COLOR_MAP.put("&n", "<underlined>");
        COLOR_MAP.put("&m", "<strikethrough>");
        COLOR_MAP.put("&k", "<obfuscated>");
    }

    private String replaceLegacyColors(final String message) {
        var result = message;
        for (final Map.Entry<String, String> entry : COLOR_MAP.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Contract(pure = true)
    @NotNull
    private String getPlaceholderKey(final String key) {
        return "%" + key + "%";
    }
}
