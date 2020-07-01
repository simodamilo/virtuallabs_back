package it.polito.ai.virtuallabs_back.repositories;

import it.polito.ai.virtuallabs_back.entities.TeamToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TeamTokenRepository extends JpaRepository<TeamToken, String> {

    List<TeamToken> findAllByExpiryDateBefore(Timestamp t);

    List<TeamToken> findAllByTeamId(Long teamId);

    boolean existsByTeamIdAndStudentSerial(Long teamId, String studentSerial);

    TeamToken getByTeamIdAndStudentSerial(Long teamId, String studentSerial);
}
