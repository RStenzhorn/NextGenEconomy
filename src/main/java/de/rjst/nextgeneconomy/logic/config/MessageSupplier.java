package de.rjst.nextgeneconomy.logic.config;

import de.rjst.nextgeneconomy.setting.NgeMessage;
import de.rjst.nextgeneconomy.setting.NgeSetting;
import de.rjst.nextgeneconomy.setting.NgePlaceholder;
import de.rjst.nextgeneconomy.util.NgeMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
@RequiredArgsConstructor
@Service("messageSupplier")
public class MessageSupplier implements BiFunction<NgeMessage, Locale, String> {

    @Qualifier("messageMap")
    private final Map<String, String> messageMap;

    private final PropertySupplier propertySupplier;

    @Override
    public String apply(final @NotNull NgeMessage message, final Locale locale) {
        final String path = message.getPath(locale);
        String result = messageMap.get(path);

        if (result == null) {
            final Locale defaultLocale = Locale.ENGLISH;
            result = messageMap.get(message.getPath(defaultLocale));
            log.warn("Unknown locale: {} using {}", locale, defaultLocale);
        }

        result = NgeMessageUtil.getMessage(result, Map.of(
                NgePlaceholder.PREFIX, propertySupplier.apply(NgeSetting.PREFIX, String.class)
        ));

        return result;
    }

}
