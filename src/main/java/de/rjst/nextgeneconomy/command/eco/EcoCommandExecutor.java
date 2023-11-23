package de.rjst.nextgeneconomy.command.eco;

import de.rjst.nextgeneconomy.model.EcoCommandRequest;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public interface EcoCommandExecutor {

    boolean execute(EcoCommandRequest request);

    String getName();
}
