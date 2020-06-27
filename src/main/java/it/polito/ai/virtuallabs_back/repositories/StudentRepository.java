package it.polito.ai.virtuallabs_back.repositories;

import it.polito.ai.virtuallabs_back.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    /*@Query("SELECT t FROM Team t INNER JOIN t.course c WHERE c=:courseName")
    Team getTeamOfCourse(String courseName);*/

}
