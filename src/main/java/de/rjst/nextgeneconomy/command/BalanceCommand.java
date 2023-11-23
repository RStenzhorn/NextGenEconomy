package de.rjst.nextgeneconomy.command;

import de.rjst.nextgeneconomy.NextGenEconomy;
import de.rjst.nextgeneconomy.api.NextGenEconomyApi;
import de.rjst.nextgeneconomy.config.bean.PluginCommand;
import de.rjst.nextgeneconomy.model.MessageRequest;
import de.rjst.nextgeneconomy.model.MessageRequestImpl;
import de.rjst.nextgeneconomy.setting.NgeMessage;
import de.rjst.nextgeneconomy.setting.NgePermission;
import de.rjst.nextgeneconomy.setting.Placeholder;
import de.rjst.nextgeneconomy.util.NgeUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
@PluginCommand(value = "balance", aliases = "money")
public class BalanceCommand implements CommandExecutor {

    private final NextGenEconomyApi nextGenEconomyApi;
    private final BukkitScheduler bukkitScheduler;

    @Qualifier("componentSupplier")
    private final Function<MessageRequest, net.kyori.adventure.text.Component> componentSupplier;

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, @NotNull final String[] args) {
        final Optional<Player> optionalPlayer = NgeUtil.getPlayer(sender);
        if (optionalPlayer.isPresent()) {
            final Player player = optionalPlayer.get();
            final Locale locale = player.locale();
            if (args.length == 0) {
                if (NgeUtil.isPlayerPermitted(player, NgePermission.CMD_BALANCE)) {
                    bukkitScheduler.runTaskAsynchronously(NextGenEconomy.getInstance(), () -> {
                        final Optional<BigDecimal> optionalBalance = nextGenEconomyApi.getBalance(player.getUniqueId());
                        if (optionalBalance.isPresent()) {
                            final BigDecimal balance = optionalBalance.get();
                            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                    .placeholders(Map.of(
                                            Placeholder.CURRENCY, nextGenEconomyApi.format(balance)))
                                    .locale(locale)
                                    .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE).build()));
                        }
                    });
                } else {
                    player.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                            .locale(locale)
                            .ngeMessage(NgeMessage.MESSAGE_ERROR_NO_PERMISSION).build()));
                }
            } else if (args.length == 1){
                if (NgeUtil.isPlayerPermitted(player, NgePermission.CMD_BALANCE_OTHER)) {
                    bukkitScheduler.runTaskAsynchronously(NextGenEconomy.getInstance(), () -> {
                        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                        final Optional<BigDecimal> optionalBalance = nextGenEconomyApi.getBalance(offlinePlayer.getUniqueId());
                        if (optionalBalance.isPresent()) {
                            final BigDecimal balance = optionalBalance.get();
                            final String playerName = Objects.requireNonNull(offlinePlayer.getName());
                            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                    .placeholders(Map.of(
                                            Placeholder.PLAYER, playerName,
                                            Placeholder.CURRENCY, nextGenEconomyApi.format(balance)))
                                    .locale(locale)
                                    .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_OTHER).build()));
                        }
                    });
                } else {
                    sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                            .locale(locale)
                            .ngeMessage(NgeMessage.MESSAGE_ERROR_NO_PERMISSION).build()));
                }
            } else {
                sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                        .locale(locale)
                        .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_USAGE).build()));
            }
        } else {
            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                    .ngeMessage(NgeMessage.MESSAGE_ERROR_NO_CONSOLE).build()));
        }
        return true;
    }
}
