package it.polito.ai.virtuallabs_back.repositories;

import it.polito.ai.virtuallabs_back.entities.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {

    List<Solution> getAllByStudentSerialAndAssignmentId(String studentSerial, Long assignmentId);

    @Query("SELECT s FROM Solution s WHERE s.assignment.id =:assignmentId " +
            "AND s.deliveryTs IN ( SELECT MAX(s1.deliveryTs) FROM Solution s1 WHERE s1.assignment.id =:assignmentId GROUP BY s1.student.serial)")
    List<Solution> getAllByAssignmentStudentAndMaxTs(Long assignmentId);

    @Query("SELECT MAX(s1.deliveryTs),s1.student.serial FROM Solution s1 WHERE s1.assignment.id =:assignmentId GROUP BY s1.student.serial")
    List<Object> getAllMaxTs(Long assignmentId);
}
