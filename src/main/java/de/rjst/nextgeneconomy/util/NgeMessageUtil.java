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
        COLOR_MAP.put("&0", "<#000000>");
        COLOR_MAP.put("&1", "<#0000AA>");
        COLOR_MAP.put("&2", "<#00AA00>");
        COLOR_MAP.put("&3", "<#00AAAA>");
        COLOR_MAP.put("&4", "<#AA0000>");
        COLOR_MAP.put("&5", "<#AA00AA>");
        COLOR_MAP.put("&6", "<#FFAA00>");
        COLOR_MAP.put("&7", "<#AAAAAA>");
        COLOR_MAP.put("&8", "<#555555>");
        COLOR_MAP.put("&9", "<#5555FF>");
        COLOR_MAP.put("&a", "<#55FF55>");
        COLOR_MAP.put("&b", "<#55FFFF>");
        COLOR_MAP.put("&c", "<#FF5555>");
        COLOR_MAP.put("&d", "<#FF55FF>");
        COLOR_MAP.put("&e", "<#FFFF55>");
        COLOR_MAP.put("&f", "<#FFFFFF>");
        COLOR_MAP.put("&r", "<reset>");
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
