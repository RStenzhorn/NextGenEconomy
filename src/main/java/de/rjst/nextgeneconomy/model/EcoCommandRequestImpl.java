package de.rjst.nextgeneconomy.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

@Builder
@Data
public class EcoCommandRequestImpl implements EcoCommandRequest {

    private Locale locale;
    private CommandSender sender;
    private UUID target;
    private BigDecimal currency;

}
