package de.rjst.nextgeneconomy.command;

import de.rjst.nextgeneconomy.NextGenEconomy;
import de.rjst.nextgeneconomy.api.NextGenEconomyApi;
import de.rjst.nextgeneconomy.config.async.AsyncExecutor;
import de.rjst.nextgeneconomy.config.bean.PluginCommand;
import de.rjst.nextgeneconomy.model.MessageRequest;
import de.rjst.nextgeneconomy.model.MessageRequestImpl;
import de.rjst.nextgeneconomy.setting.NgeMessage;
import de.rjst.nextgeneconomy.setting.NgePermission;
import de.rjst.nextgeneconomy.setting.NgePlaceholder;
import de.rjst.nextgeneconomy.util.NgeUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
@PluginCommand(value = "balance", aliases = "money")
public class BalanceCommand implements CommandExecutor {

    private final NextGenEconomyApi nextGenEconomyApi;
    private final AsyncExecutor asyncExecutor;

    @Qualifier("componentSupplier")
    private final Function<MessageRequest, Component> componentSupplier;

    private final BiFunction<NgeMessage, Locale, Component> simpleComponentSupplier;


    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, @NotNull final String[] args) {
        final Optional<Player> optionalPlayer = NgeUtil.getPlayer(sender);
        if (optionalPlayer.isPresent()) {
            final Player player = optionalPlayer.get();
            final Locale locale = player.locale();
            if (args.length == 0) {
                if (NgeUtil.isPermitted(player, NgePermission.CMD_BALANCE)) {
                    asyncExecutor.accept(() -> {
                        final Optional<BigDecimal> optionalBalance = nextGenEconomyApi.getBalance(player.getUniqueId());
                        if (optionalBalance.isPresent()) {
                            final BigDecimal balance = optionalBalance.get();
                            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                    .placeholders(Map.of(
                                            NgePlaceholder.CURRENCY, nextGenEconomyApi.format(balance)))
                                    .locale(locale)
                                    .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE).build()));
                        }
                    });
                } else {
                    sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_NO_PERMISSION, locale));
                }
            } else if (args.length == 1) {
                if (NgeUtil.isPermitted(player, NgePermission.CMD_BALANCE_OTHER)) {
                    asyncExecutor.accept(() -> {
                        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                        final Optional<BigDecimal> optionalBalance = nextGenEconomyApi.getBalance(offlinePlayer.getUniqueId());
                        if (optionalBalance.isPresent() && offlinePlayer.hasPlayedBefore()) {
                            final BigDecimal balance = optionalBalance.get();
                            final String playerName = Objects.requireNonNull(offlinePlayer.getName());
                            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                    .placeholders(Map.of(
                                            NgePlaceholder.PLAYER, playerName,
                                            NgePlaceholder.CURRENCY, nextGenEconomyApi.format(balance)))
                                    .locale(locale)
                                    .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_OTHER).build()));
                        } else {
                            sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_PLAYER_NOT_FOUND, locale));
                        }
                    });
                } else {
                    sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_NO_PERMISSION, locale));
                }
            } else {
                sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_CMD_BALANCE_USAGE, locale));
            }
        } else {
            sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_NO_CONSOLE, null));
        }

        return true;
    }
}
