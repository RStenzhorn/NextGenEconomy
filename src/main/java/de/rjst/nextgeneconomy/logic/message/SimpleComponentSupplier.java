package de.rjst.nextgeneconomy.logic.message;

import de.rjst.nextgeneconomy.model.MessageRequest;
import de.rjst.nextgeneconomy.model.MessageRequestImpl;
import de.rjst.nextgeneconomy.setting.NgeMessage;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class SimpleComponentSupplier implements BiFunction<NgeMessage, Locale, Component> {

    private final Function<MessageRequest, Component> componentSupplier;

    @Override
    public Component apply(final NgeMessage ngeMessage, final Locale locale) {
        return componentSupplier.apply(MessageRequestImpl.builder()
                .ngeMessage(ngeMessage)
                .locale(locale)
                .build());
    }
}
