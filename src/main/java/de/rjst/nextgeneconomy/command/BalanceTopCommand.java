package de.rjst.nextgeneconomy.command;

import de.rjst.nextgeneconomy.api.NextGenEconomyApi;
import de.rjst.nextgeneconomy.config.bean.PluginCommand;
import de.rjst.nextgeneconomy.database.repository.EconomyPlayerRepository;
import de.rjst.nextgeneconomy.database.unit.EconomyPlayerUnit;
import de.rjst.nextgeneconomy.logic.config.PropertySupplier;
import de.rjst.nextgeneconomy.model.MessageRequest;
import de.rjst.nextgeneconomy.model.MessageRequestImpl;
import de.rjst.nextgeneconomy.setting.NgeMessage;
import de.rjst.nextgeneconomy.setting.NgePermission;
import de.rjst.nextgeneconomy.setting.NgeSetting;
import de.rjst.nextgeneconomy.setting.Placeholder;
import de.rjst.nextgeneconomy.util.NgeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;


@Slf4j
@RequiredArgsConstructor
@Component
@PluginCommand("balancetop")
public class BalanceTopCommand implements CommandExecutor {

    private final NextGenEconomyApi nextGenEconomyApi;

    private static final String SORT_FIELD = "balance";

    private final PropertySupplier propertySupplier;

    @Qualifier("balanceTopSupplier")
    private final Function<Integer, Page<EconomyPlayerUnit>> balanceTopSupplier;

    @Qualifier("componentSupplier")
    private final Function<MessageRequest, net.kyori.adventure.text.Component> componentSupplier;


    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, @NotNull final String[] args) {
        final Optional<Player> optionalPlayer = NgeUtil.getPlayer(sender);
        if (optionalPlayer.isPresent()) {
            final Player player = optionalPlayer.get();
            final Locale locale = player.locale();
            if (NgeUtil.isPlayerPermitted(player, NgePermission.CMD_BALANCE_TOP)) {
                if (args.length == 0) {
                    final Page<EconomyPlayerUnit> page = balanceTopSupplier.apply(0);
                    sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                            .placeholders(Map.of(
                                    Placeholder.PAGE, "1",
                                    Placeholder.MAX_PAGE, String.valueOf(page.getTotalPages())
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
                                            Placeholder.PAGE, String.valueOf(pageShow),
                                            Placeholder.MAX_PAGE, String.valueOf(totalPages)
                                    ))
                                    .locale(locale)
                                    .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_TOP_HEADER).build()));
                            sendPageMessage(sender, locale, pageNumber, page);
                        } else {
                            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                    .locale(locale)
                                    .ngeMessage(NgeMessage.MESSAGE_ERROR_INVALID_PAGE).build()));
                        }
                    } else {
                        sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                .locale(locale)
                                .ngeMessage(NgeMessage.MESSAGE_ERROR_INVALID_PAGE).build()));
                    }
                } else {
                    sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                            .locale(locale)
                            .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_TOP_USAGE).build()));
                }
            } else {
                sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                        .locale(locale)
                        .ngeMessage(NgeMessage.MESSAGE_ERROR_NO_PERMISSION).build()));
            }
        } else {
            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                    .ngeMessage(NgeMessage.MESSAGE_ERROR_NO_CONSOLE).build()));
        }
        return true;
    }

    private void sendPageMessage(@NotNull final Audience sender, final Locale locale, final int pageNumber, final @NotNull Slice<EconomyPlayerUnit> page) {
        int rank = getRank(pageNumber, page.getSize());

        for (final EconomyPlayerUnit unit : page) {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(unit.getId());
            final BigDecimal balance = unit.getBalance();
            log.info("UUID: {}", unit.getId());
            if (offlinePlayer.hasPlayedBefore()) {
                sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                        .placeholders(Map.of(
                                Placeholder.POSITION, String.valueOf(rank),
                                Placeholder.PLAYER, Objects.requireNonNull(offlinePlayer.getName()),
                                Placeholder.CURRENCY, nextGenEconomyApi.format(balance)))
                        .locale(locale)
                        .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_TOP).build()));
            }
            rank++;
        }
    }

    private Pageable getPageable(final Integer page) {
        final int size = propertySupplier.apply(NgeSetting.BALANCE_TOP_PAGE_SIZE, Integer.class);
        final Sort sort = Sort.by(SORT_FIELD).descending();
       return PageRequest.of(page, size, sort);
    }

    private static int getRank(final int pageNumber, final int size) {
        int result = 1;
        if (0 < pageNumber) {
            result = result + pageNumber * size;
        }
        return result;
    }
}
