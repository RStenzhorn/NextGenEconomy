package de.rjst.nextgeneconomy.command;

import de.rjst.nextgeneconomy.api.NextGenEconomyApi;
import de.rjst.nextgeneconomy.config.bean.PluginCommand;
import de.rjst.nextgeneconomy.database.unit.EconomyPlayerUnit;
import de.rjst.nextgeneconomy.model.MessageRequest;
import de.rjst.nextgeneconomy.model.MessageRequestImpl;
import de.rjst.nextgeneconomy.setting.NgeMessage;
import de.rjst.nextgeneconomy.setting.NgePermission;
import de.rjst.nextgeneconomy.setting.NgePlaceholder;
import de.rjst.nextgeneconomy.util.NgeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;


@Slf4j
@RequiredArgsConstructor
@Service
@PluginCommand(value = "balancetop", permission = NgePermission.CMD_BALANCE_TOP)
public class BalanceTopCommand implements CommandExecutor {

    private final NextGenEconomyApi nextGenEconomyApi;
    private static final String FIRST_PAGE_NUMBER = "1";

    @Qualifier("balanceTopSupplier")
    private final Function<Integer, Page<EconomyPlayerUnit>> balanceTopSupplier;

    @Qualifier("componentSupplier")
    private final Function<MessageRequest, Component> componentSupplier;

    private final BiFunction<NgeMessage, Locale, Component> simpleComponentSupplier;


    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, @NotNull final String[] args) {
        final Optional<Player> optionalPlayer = NgeUtil.getPlayer(sender);
        if (optionalPlayer.isPresent()) {
            final Player player = optionalPlayer.get();
            final Locale locale = player.locale();
            if (NgeUtil.isPermitted(player, NgePermission.CMD_BALANCE_TOP)) {
                if (args.length == 0) {
                    final Page<EconomyPlayerUnit> page = balanceTopSupplier.apply(0);
                    sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                            .placeholders(Map.of(
                                    NgePlaceholder.PAGE, FIRST_PAGE_NUMBER,
                                    NgePlaceholder.MAX_PAGE, String.valueOf(page.getTotalPages())
                            ))
                            .locale(locale)
                            .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_TOP_HEADER).build()));
                    sendPageMessage(sender, locale, 0, page);
                } else if (args.length == 1) {
                    final Optional<Integer> optionalPageNumber = NgeUtil.getPageNumber(args[0]);
                    if (optionalPageNumber.isPresent()) {
                        Integer pageNumber = optionalPageNumber.get();
                        final int pageShow = pageNumber;
                        pageNumber--;

                        final Page<EconomyPlayerUnit> page = balanceTopSupplier.apply(pageNumber);
                        final int totalPages = page.getTotalPages();
                        if (pageShow <= totalPages) {
                            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                    .placeholders(Map.of(
                                            NgePlaceholder.PAGE, String.valueOf(pageShow),
                                            NgePlaceholder.MAX_PAGE, String.valueOf(totalPages)
                                    ))
                                    .locale(locale)
                                    .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_TOP_HEADER).build()));
                            sendPageMessage(sender, locale, pageNumber, page);
                        } else {
                            sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_INVALID_PAGE, locale));
                        }
                    } else {
                        sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_INVALID_PAGE, locale));
                    }
                } else {
                    sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_CMD_BALANCE_TOP_USAGE, locale));
                }
            } else {
                sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_NO_PERMISSION, locale));
            }
        } else {
            sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_NO_CONSOLE, null));
        }
        return true;
    }

    private void sendPageMessage(@NotNull final Audience sender, final Locale locale, final int pageNumber, final @NotNull Slice<EconomyPlayerUnit> page) {
        int rank = getRank(pageNumber, page.getSize());

        for (final EconomyPlayerUnit unit : page.getContent()) {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(unit.getId());
            final BigDecimal balance = unit.getBalance();
            if (offlinePlayer.hasPlayedBefore()) {
                sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                        .placeholders(Map.of(
                                NgePlaceholder.POSITION, String.valueOf(rank),
                                NgePlaceholder.PLAYER, Objects.requireNonNull(offlinePlayer.getName()),
                                NgePlaceholder.CURRENCY, nextGenEconomyApi.format(balance)))
                        .locale(locale)
                        .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_TOP).build()));
            }
            rank++;
        }
    }

    private static int getRank(final int pageNumber, final int size) {
        int result = 1;
        if (0 < pageNumber) {
            result = result + pageNumber * size;
        }
        return result;
    }
}
