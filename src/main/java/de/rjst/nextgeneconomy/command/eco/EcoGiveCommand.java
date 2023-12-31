package de.rjst.nextgeneconomy.command.eco;

import de.rjst.nextgeneconomy.api.NextGenEconomyApi;
import de.rjst.nextgeneconomy.model.EcoCommandRequest;
import de.rjst.nextgeneconomy.model.MessageRequest;
import de.rjst.nextgeneconomy.model.MessageRequestImpl;
import de.rjst.nextgeneconomy.setting.NgeMessage;
import de.rjst.nextgeneconomy.setting.NgePlaceholder;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
@Controller
public class EcoGiveCommand implements EcoCommandExecutor {

    private final NextGenEconomyApi nextGenEconomyApi;

    @Qualifier("componentSupplier")
    private final Function<MessageRequest, Component> componentSupplier;

    private final BiFunction<NgeMessage, Locale, Component> simpleComponentSupplier;

    @Override
    public boolean execute(final @NotNull EcoCommandRequest request) {
        final CommandSender sender = request.getSender();
        final Locale locale = request.getLocale();
        final BigDecimal currency = request.getCurrency();
        final UUID target = request.getTarget();
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);

        if (nextGenEconomyApi.addBalance(target, currency)) {
            sender.sendMessage(componentSupplier.apply(MessageRequestImpl.builder()
                    .placeholders(Map.of(
                            NgePlaceholder.PLAYER, Objects.requireNonNull(offlinePlayer.getName()),
                            NgePlaceholder.CURRENCY, nextGenEconomyApi.format(currency)))
                    .locale(locale)
                    .ngeMessage(NgeMessage.MESSAGE_CMD_ECO_GIVE).build()));
        } else {
            sender.sendMessage(simpleComponentSupplier.apply(NgeMessage.MESSAGE_ERROR_ADMIN, locale));
        }


        return true;
    }

    @Override
    public String getName() {
        return "give";
    }
}
