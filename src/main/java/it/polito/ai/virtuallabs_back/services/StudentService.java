package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface StudentService {

    /**
     * @param studentSerial
     * @return
     */
    Optional<StudentDTO> getStudent(String studentSerial);

    /**
     * @return
     */
    List<StudentDTO> getAllStudents();

    /**
     * With the getEnrolledStudents method all students enrolled to the specific course are returned.
     *
     * @param courseName it is the course for which the user want the enrolled students.
     * @return list of enrolled students.
     */
    List<StudentDTO> getEnrolledStudents(String courseName);

    /**
     * @param courseName
     * @return
     */
    List<StudentDTO> getAvailableStudents(String courseName);

    /**
     * @param courseName
     * @return
     */
    List<StudentDTO> getEngagedStudents(String courseName);

    /**
     * @param teamId
     * @return
     */
    @PreAuthorize("hasRole('STUDENT')")
    List<StudentDTO> getTeamStudents(Long teamId);

    /**
     * With the addStudentToCourse method a student is enrolled to a specific course.
     *
     * @param studentSerial it is the serial of the student that must be enrolled.
     * @param courseName    it is the name of the course in which the student must be enrolled.
     * @return the enrolled studentDTO is returned, if it is not possible the studentDTO is null.
     */
    @PreAuthorize("hasRole('TEACHER')")
    StudentDTO addStudentToCourse(String studentSerial, String courseName);

    /**
     * With the enrollAll method all students are enrolled to the specific course,
     * it invokes the addStudentToCourse method for each student in the list.
     *
     * @param studentSerials it is a list of student serials that must be added to the course.
     * @param courseName     it is the name of the course in which students must be enrolled.
     * @return it returns a list of studentDTOs, if a problem occurs for one student,
     * null will be inserted inside the list.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<StudentDTO> enrollAll(List<String> studentSerials, String courseName);

    /**
     * With the enrollAll method all students are enrolled to the specific course,
     * it invokes the enrollAll by passing a list of student serials and the course name.
     *
     * @param reader     it is used to take all the student from the csv file.
     * @param courseName it is the name of the course in which students must be enrolled.
     * @return it returns a list of studentDTOs, if a problem occurs for one student,
     * null will be inserted inside the list.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<StudentDTO> enrollCsv(Reader reader, String courseName);

    /**
     * @param image
     * @return
     */
    StudentDTO uploadImage(byte[] image);

    /**
     * If the course exists and the teacher has the permissions, the student is deleted
     * from the course.
     *
     * @param studentSerial it is the serial of the student that must be deleted from the course.
     * @param courseName    it is the name of the course from which student must be enrolled.
     */
    @PreAuthorize("hasRole('TEACHER')")
    void deleteStudentFromCourse(String studentSerial, String courseName);
}
