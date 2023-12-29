package de.rjst.nextgeneconomy.command.eco;

import de.rjst.nextgeneconomy.api.NextGenEconomyApi;
import de.rjst.nextgeneconomy.config.async.AsyncExecutor;
import de.rjst.nextgeneconomy.config.bean.PluginCommand;
import de.rjst.nextgeneconomy.model.EcoCommandRequestImpl;
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
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@PluginCommand(value = "eco", permission = NgePermission.CMD_ECO)
@Service
public class EcoCommand implements CommandExecutor {

    private final NextGenEconomyApi nextGenEconomyApi;

    private final List<EcoCommandExecutor> ecoCommandExecutors;
    private final AsyncExecutor asyncExecutor;

    @Qualifier("componentSupplier")
    private final Function<MessageRequest, Component> componentSupplier;

    private final BiFunction<NgeMessage, Locale, Component> simpleComponentSupplier;

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        final Locale locale;
        final Optional<Player> player = NgeUtil.getPlayer(sender);
        locale = player.map(Player::locale).orElseGet(Locale::getDefault);

        if (NgeUtil.isPermitted(sender, NgePermission.CMD_ECO)) {
            if (args.length == 3) {
                final Optional<BigDecimal> optionalCurrency = NgeUtil.getBigDecimal(args[2]);
                if (optionalCurrency.isPresent()) {
                    asyncExecutor.accept(() -> {
                        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                        final UUID uuid = offlinePlayer.getUniqueId();
                        if (nextGenEconomyApi.hasAccount(uuid)) {
                            final Optional<EcoCommandExecutor> commandExecutorOptional = getSubCommand(args[0]);
                            if (commandExecutorOptional.isPresent()) {
                                final EcoCommandExecutor ecoCommandExecutor = commandExecutorOptional.get();
                                ecoCommandExecutor.execute(EcoCommandRequestImpl.builder()
                                        .target(uuid)
                                        .currency(optionalCurrency.get())
                                        .sender(sender)
                                        .locale(locale)
                                        .build());
                            } else {
                                sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_CMD_ECO_USAGE, locale));
                            }
                        }
                    });
                } else {
                    sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                            .placeholders(Map.of(
                                    NgePlaceholder.CURRENCY_PLURAL, nextGenEconomyApi.getCurrencyPlural()
                            ))
                            .locale(locale)
                            .ngeMessage(NgeMessage.MESSAGE_ERROR_INVALID_CURRENCY).build()));
                }
            } else {
                sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_CMD_ECO_USAGE, locale));
            }
        } else {
            sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_NO_PERMISSION, locale));
        }

        return true;
    }

    private Optional<EcoCommandExecutor> getSubCommand(final @NonNls String subCommandName) {
        EcoCommandExecutor result = null;
        for (final EcoCommandExecutor ecoCommandExecutor : ecoCommandExecutors) {
            if (subCommandName.equalsIgnoreCase(ecoCommandExecutor.getName())) {
                result = ecoCommandExecutor;
                break;
            }
        }
        return Optional.ofNullable(result);
    }
}
