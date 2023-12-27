package de.rjst.nextgeneconomy.logic.message;

import de.rjst.nextgeneconomy.model.MessageRequest;
import de.rjst.nextgeneconomy.setting.NgeMessage;
import de.rjst.nextgeneconomy.util.NgeMessageUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
@Service("componentSupplier")
public class ComponentSupplier implements Function<MessageRequest, Component> {

    @Qualifier("messageSupplier")
    private final BiFunction<NgeMessage, Locale, String> messageSupplier;

    @Override
    public final @NotNull Component apply(final @NotNull MessageRequest request) {
        final Locale locale = request.getLocale() == null ? Locale.getDefault() : request.getLocale();
        final String baseMessage = messageSupplier.apply(request.getNgeMessage(), locale);
        final String message = NgeMessageUtil.getMessage(baseMessage,
                request.getPlaceholders() == null ? Map.of() : request.getPlaceholders());
        return NgeMessageUtil.getComponent(message);
    }
}
