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
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@PluginCommand("pay")
@Component
public class PayCommand implements CommandExecutor {

    private final NextGenEconomyApi nextGenEconomyApi;
    private final BukkitScheduler bukkitScheduler;

    @Qualifier("componentSupplier")
    private final Function<MessageRequest, net.kyori.adventure.text.Component> componentSupplier;

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        final Optional<Player> optionalPlayer = NgeUtil.getPlayer(sender);
        if (optionalPlayer.isPresent()) {
            final Player player = optionalPlayer.get();
            final Locale locale = player.locale();
            if (args.length == 2) {
                if (NgeUtil.isPlayerPermitted(player, NgePermission.CMD_PAY)) {
                    bukkitScheduler.runTaskAsynchronously(NextGenEconomy.getInstance(), () -> {
                        final UUID playerUuid = player.getUniqueId();
                        final String targetPlayer = args[0];
                        final Optional<BigDecimal> optionalCurrency = NgeUtil.getBigDecimal(args[1]);

                        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetPlayer);
                        final UUID targetUuid = offlinePlayer.getUniqueId();

                        if (optionalCurrency.isPresent()) {
                            final BigDecimal amount = optionalCurrency.get();
                            if (nextGenEconomyApi.hasAccount(targetUuid)) {
                                if (nextGenEconomyApi.removeBalance(playerUuid, amount)) {
                                    if (nextGenEconomyApi.addBalance(targetUuid, amount)) {
                                        sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                                .placeholders(Map.of(
                                                        Placeholder.PLAYER, Objects.requireNonNull(offlinePlayer.getName()),
                                                        Placeholder.CURRENCY, nextGenEconomyApi.format(amount)
                                                ))
                                                .locale(locale)
                                                .ngeMessage(NgeMessage.MESSAGE_CMD_PAY_SOURCE).build()));
                                        final Optional<Player> onlineTargetPlayer = NgeUtil.getPlayer(targetUuid);
                                        onlineTargetPlayer.ifPresent(value -> value.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                                .placeholders(Map.of(
                                                        Placeholder.PLAYER, player.getName(),
                                                        Placeholder.CURRENCY, nextGenEconomyApi.format(amount)
                                                ))
                                                .locale(locale)
                                                .ngeMessage(NgeMessage.MESSAGE_CMD_PAY_TARGET).build())));
                                    } else {
                                        log.error("Transaction Source {} -> {}: {}", playerUuid, targetPlayer, amount);
                                        sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                                .locale(locale)
                                                .ngeMessage(NgeMessage.MESSAGE_ERROR_ADMIN).build()));
                                    }
                                } else {
                                    sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                            .placeholders(Map.of(
                                                    Placeholder.CURRENCY_PLURAL, nextGenEconomyApi.getCurrencyPlural()
                                            ))
                                            .locale(locale)
                                            .ngeMessage(NgeMessage.MESSAGE_ERROR_NOT_ENOUGH).build()));
                                }
                            } else {
                                sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                        .locale(locale)
                                        .ngeMessage(NgeMessage.MESSAGE_ERROR_PLAYER_NOT_FOUND).build()));
                            }
                        } else {
                            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                    .locale(locale)
                                    .ngeMessage(NgeMessage.MESSAGE_ERROR_INVALID_CURRENCY).build()));
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
                        .ngeMessage(NgeMessage.MESSAGE_CMD_PAY_USAGE).build()));
            }
        } else {
            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                    .ngeMessage(NgeMessage.MESSAGE_ERROR_NO_CONSOLE).build()));
        }

        return true;
    }
}
