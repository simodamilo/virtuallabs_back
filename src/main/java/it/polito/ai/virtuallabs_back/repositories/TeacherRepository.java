package it.polito.ai.virtuallabs_back.repositories;

import it.polito.ai.virtuallabs_back.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {
}
