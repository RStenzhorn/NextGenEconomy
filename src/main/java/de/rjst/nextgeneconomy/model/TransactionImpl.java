package de.rjst.nextgeneconomy.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;


@Builder
@Data
public class TransactionImpl implements Transaction {

    private UUID target;
    private TransactionType type;
    private BigDecimal currency;

}
