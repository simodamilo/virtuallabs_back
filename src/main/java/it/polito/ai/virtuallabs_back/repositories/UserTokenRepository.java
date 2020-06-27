package it.polito.ai.virtuallabs_back.repositories;

import it.polito.ai.virtuallabs_back.entities.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, String> {

    List<UserToken> findAllByExpiryDateBefore(Timestamp t);

}
