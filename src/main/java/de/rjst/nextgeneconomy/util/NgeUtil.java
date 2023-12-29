package de.rjst.nextgeneconomy.util;

import de.rjst.nextgeneconomy.setting.NgePermission;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class NgeUtil {

    private final char OLD_CHAR = ',';
    private final char NEW_CHAR = '.';


    @NotNull
    public Optional<BigDecimal> getBigDecimal(@NotNull final String currency) {
        @Nullable BigDecimal result = null;
        try {
            final String replace = currency.replace(OLD_CHAR, NEW_CHAR);
            result = new BigDecimal(replace).setScale(2, RoundingMode.FLOOR);
            if (result.compareTo(BigDecimal.ZERO) <= 0) {
                result = null;
            }
        } catch (final NumberFormatException ignored) {
        }

        return Optional.ofNullable(result);
    }

    @NotNull
    public Optional<Integer> getPageNumber(final String value) {
        @Nullable Integer result = null;
        try {
            result = Integer.valueOf(value);
            if (result <= 0) {
                result = null;
            }
        } catch (final NumberFormatException ignored) {}

        return Optional.ofNullable(result);
    }

    @NotNull
    public Optional<Player> getPlayer(@NotNull final CommandSender sender) {
        Player result = null;

        if (sender instanceof final Player player) {
            result = player;
        }
        return Optional.ofNullable(result);
    }

    @NotNull
    public Optional<Player> getPlayer(@NotNull final UUID uuid) {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }


    public boolean isPermitted(final Permissible player, final NgePermission ngePermission) {
        return player.hasPermission(ngePermission.getPermission());
    }

    public @NotNull List<String> getOfflinePlayers() {
        final List<String> result = new LinkedList<>();
        final OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        for (final OfflinePlayer offlinePlayer : offlinePlayers) {
            result.add(offlinePlayer.getName());
        }
        return result;
    }

    public int getArgsLength(@NotNull final String command) {
        return command.split(" ").length;
    }
}
