package de.rjst.nextgeneconomy.model;

import de.rjst.nextgeneconomy.setting.NgeMessage;

import java.util.Locale;
import java.util.Map;

public interface MessageRequest {

    NgeMessage getNgeMessage();

    Locale getLocale();

    Map<String, String> getPlaceholders();

}
