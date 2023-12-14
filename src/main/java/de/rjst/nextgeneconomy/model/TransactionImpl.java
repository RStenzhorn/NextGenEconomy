package de.rjst.nextgeneconomy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;


@Builder
@AllArgsConstructor
@Data
public class TransactionImpl implements Transaction {

    private UUID target;
    private TransactionType type;
    private BigDecimal currency;

}
