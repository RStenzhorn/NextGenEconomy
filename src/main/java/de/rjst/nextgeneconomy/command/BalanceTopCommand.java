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
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;


@RequiredArgsConstructor
@Component
@PluginCommand("balancetop")
public class BalanceTopCommand implements CommandExecutor {

    private static final String SORT_FIELD = "balance";

    private final NextGenEconomyApi nextGenEconomyApi;
    private final EconomyPlayerRepository economyPlayerRepository;

    private final PropertySupplier propertySupplier;

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
                    final int pageNumber = 0;

                    final int size = propertySupplier.apply(NgeSetting.BALANCE_TOP_PAGE_SIZE, Integer.class);
                    final Sort sort = Sort.by(SORT_FIELD).descending();
                    final PageRequest pageable = PageRequest.of(pageNumber, size, sort);
                    final Page<EconomyPlayerUnit> page = economyPlayerRepository.findAll(pageable);

                    sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                            .placeholders(Map.of(
                                    Placeholder.PAGE, "1",
                                    Placeholder.MAX_PAGE, String.valueOf(page.getTotalPages())
                            ))
                            .locale(locale)
                            .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_TOP_HEADER).build()));

                    sendPageMessage(sender, locale, pageNumber, size, page);
                } else if (args.length == 1) {
                    final Optional<Integer> optionalPageNumber = NgeUtil.getPageNumber(args[0]);
                    if (optionalPageNumber.isPresent()) {
                        int pageNumber = optionalPageNumber.get();
                        final int pageShow = pageNumber;
                        pageNumber--;

                        final int size = propertySupplier.apply(NgeSetting.BALANCE_TOP_PAGE_SIZE, Integer.class);
                        final Sort sort = Sort.by(SORT_FIELD).descending();
                        final PageRequest pageable = PageRequest.of(pageNumber, size, sort);
                        final Page<EconomyPlayerUnit> page = economyPlayerRepository.findAll(pageable);
                        final int totalPages = page.getTotalPages();
                        if (pageShow <= totalPages) {
                            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                                    .placeholders(Map.of(
                                            Placeholder.PAGE, String.valueOf(pageShow),
                                            Placeholder.MAX_PAGE, String.valueOf(totalPages)
                                    ))
                                    .locale(locale)
                                    .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_TOP_HEADER).build()));
                            sendPageMessage(sender, locale, pageNumber, size, page);
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

    private void sendPageMessage(@NotNull final CommandSender sender, final Locale locale, final int pageNumber, final int size, final @NotNull Page<EconomyPlayerUnit> page) {
        int rank = getRank(pageNumber, size);

        for (final EconomyPlayerUnit unit : page) {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(unit.getId());
            final BigDecimal balance = unit.getBalance();

            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                    .placeholders(Map.of(
                            Placeholder.POSITION, String.valueOf(rank),
                            Placeholder.PLAYER, Objects.requireNonNull(offlinePlayer.getName()),
                            Placeholder.CURRENCY, nextGenEconomyApi.format(balance)))
                    .locale(locale)
                    .ngeMessage(NgeMessage.MESSAGE_CMD_BALANCE_TOP).build()));
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
