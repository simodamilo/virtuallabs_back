package it.polito.ai.virtuallabs_back.repositories;

import it.polito.ai.virtuallabs_back.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUsername(String username);
}
