package de.rjst.nextgeneconomy.model;

import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

public interface EcoCommandRequest {

    CommandSender getSender();

    Locale getLocale();

    UUID getTarget();

    BigDecimal getCurrency();
}
