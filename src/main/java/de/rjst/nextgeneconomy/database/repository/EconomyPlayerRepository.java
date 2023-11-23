package de.rjst.nextgeneconomy.database.repository;

import de.rjst.nextgeneconomy.database.unit.EconomyPlayerUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EconomyPlayerRepository extends JpaRepository<EconomyPlayerUnit, UUID> {

}
