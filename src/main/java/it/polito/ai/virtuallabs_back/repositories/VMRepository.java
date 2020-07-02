package it.polito.ai.virtuallabs_back.repositories;


import it.polito.ai.virtuallabs_back.entities.Team;
import it.polito.ai.virtuallabs_back.entities.VM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VMRepository extends JpaRepository<VM, Long> {

    int countVMSByTeamAndActiveTrue(Team team);
}
