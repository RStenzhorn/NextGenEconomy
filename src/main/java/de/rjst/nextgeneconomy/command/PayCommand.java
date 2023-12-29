package de.rjst.nextgeneconomy.command;

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
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@PluginCommand(value = "pay", permission = NgePermission.CMD_PAY)
@Service
public class PayCommand implements CommandExecutor {

    private final NextGenEconomyApi nextGenEconomyApi;
    private final AsyncExecutor asyncExecutor;

    @Qualifier("componentSupplier")
    private final Function<MessageRequest, Component> componentSupplier;

    private final BiFunction<NgeMessage, Locale, Component> simpleComponentSupplier;

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        final Optional<Player> optionalPlayer = NgeUtil.getPlayer(sender);
        if (optionalPlayer.isPresent()) {
            final Player player = optionalPlayer.get();
            final Locale locale = player.locale();
            if (args.length == 2) {
                if (NgeUtil.isPermitted(player, NgePermission.CMD_PAY)) {
                    asyncExecutor.accept(() -> {
                        final UUID playerUuid = player.getUniqueId();
                        final String targetPlayer = args[0];
                        final Optional<BigDecimal> optionalCurrency = NgeUtil.getBigDecimal(args[1]);

                        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetPlayer);
                        final UUID targetUuid = offlinePlayer.getUniqueId();

                        if (optionalCurrency.isPresent()) {
                            final BigDecimal amount = optionalCurrency.get();
                            if (nextGenEconomyApi.hasAccount(targetUuid) && offlinePlayer.hasPlayedBefore()) {
                                if (nextGenEconomyApi.removeBalance(playerUuid, amount)) {
                                    if (nextGenEconomyApi.addBalance(targetUuid, amount)) {
                                        sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                                .placeholders(Map.of(
                                                        NgePlaceholder.PLAYER, Objects.requireNonNull(offlinePlayer.getName()),
                                                        NgePlaceholder.CURRENCY, nextGenEconomyApi.format(amount)
                                                ))
                                                .locale(locale)
                                                .ngeMessage(NgeMessage.MESSAGE_CMD_PAY_SOURCE).build()));
                                        final Optional<Player> onlineTargetPlayer = NgeUtil.getPlayer(targetUuid);
                                        onlineTargetPlayer.ifPresent(value -> value.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                                .placeholders(Map.of(
                                                        NgePlaceholder.PLAYER, player.getName(),
                                                        NgePlaceholder.CURRENCY, nextGenEconomyApi.format(amount)
                                                ))
                                                .locale(locale)
                                                .ngeMessage(NgeMessage.MESSAGE_CMD_PAY_TARGET).build())));
                                    } else {
                                        log.error("Transaction Source {} -> {}: {}", playerUuid, targetPlayer, amount);
                                        sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_ADMIN, locale));
                                    }
                                } else {
                                    sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                            .placeholders(Map.of(
                                                    NgePlaceholder.CURRENCY_PLURAL, nextGenEconomyApi.getCurrencyPlural()
                                            ))
                                            .locale(locale)
                                            .ngeMessage(NgeMessage.MESSAGE_ERROR_NOT_ENOUGH).build()));
                                }
                            } else {
                                sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_PLAYER_NOT_FOUND, locale));
                            }
                        } else {
                            sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_INVALID_CURRENCY, locale));
                        }
                    });
                } else {
                    sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_NO_PERMISSION, locale));
                }
            } else {
                sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_CMD_PAY_USAGE, locale));
            }
        } else {
            sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_NO_CONSOLE, null));
        }

        return true;
    }
}
