package de.rjst.nextgeneconomy.database.unit;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "economy_player")
public class EconomyPlayerUnit {

    @Id
    @Column(name = "uuid", nullable = false)
    private UUID id;

    private BigDecimal balance;
}
