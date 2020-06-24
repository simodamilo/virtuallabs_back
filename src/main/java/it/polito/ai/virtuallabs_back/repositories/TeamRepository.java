package it.polito.ai.virtuallabs_back.repositories;

import it.polito.ai.virtuallabs_back.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}
