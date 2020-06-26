package it.polito.ai.virtuallabs_back.repositories;

import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("SELECT s FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name=:courseName")
    List<Student> getStudentsInTeams(String courseName);

    @Query("SELECT s FROM Student s INNER JOIN s.courses c WHERE c.name=:courseName " +
            "AND s NOT IN (SELECT s FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name=:courseName)")
    List<Student> getStudentsNotInTeams(String courseName);

    @Query("SELECT c FROM Course c INNER JOIN c.teachers t WHERE t.serial=:teacherSerial")
    List<Course> getTeacherCourses(String teacherSerial);
}
