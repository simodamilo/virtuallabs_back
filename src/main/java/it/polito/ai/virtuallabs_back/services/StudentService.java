package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamTokenDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface StudentService {

    /**
     * @param studentId
     * @return
     */
    Optional<StudentDTO> getStudent(String studentId);

    /**
     * Used to get the profile image of a student by the serial.
     *
     * @param studentSerial of the desired student.
     * @return empty optional if the student misses.
     */
    byte[] getStudentImage(String studentSerial);

    /**
     * Used to get the list of students.
     *
     * @return list of all students.
     */
    List<StudentDTO> getAllStudents(String courseName);

    /**
     * Used to get all the team that are enrolled in the course.
     *
     * @param courseName in which students are searched.
     * @return list of enrolled students.
     */
    List<StudentDTO> getEnrolledStudents(String courseName);

    /**
     * Used to get the owner of an solution.
     *
     * @param solutionId of the solution searched.
     * @return the owner of the solution.
     */
    StudentDTO getSolutionStudent(Long solutionId);

    /**
     * Used to get all the students without team for the course.
     *
     * @param courseName in which students are searched.
     * @return list of students available for the course.
     */
    List<StudentDTO> getAvailableStudents(String courseName);

    /**
     * Used to get all the students that already have a team for the course.
     *
     * @param courseName in which students are searched.
     * @return list of students engaged for the course.
     */
    List<StudentDTO> getEngagedStudents(String courseName);

    /**
     * Used to get the members of a team.
     *
     * @param teamId of the team selected.
     * @return list of students engaged in the team passed.
     */
    List<StudentDTO> getTeamStudents(Long teamId);

    /**
     * Used to get the owners of the vm.
     *
     * @param vmId of the vm selected.
     * @return list of students that own the vm.
     */
    List<StudentDTO> getVmOwners(Long vmId);

    /**
     * Used to get the status of a student
     *
     * @param teamId        of the team in which the student is.
     * @param studentSerial of the searched student
     * @return the student status.
     */
    TeamTokenDTO getStudentTeamStatus(Long teamId, String studentSerial);

    /**
     * Used to enroll a student to the course passed.
     *
     * @param studentSerial to identify the student.
     * @param courseName    in which student is added.
     * @return the student that was added to the course.
     */
    @PreAuthorize("hasRole('TEACHER')")
    StudentDTO addStudentToCourse(String studentSerial, String courseName);

    /**
     * Used to enroll a list of students.
     *
     * @param studentSerials list of serial that must be added to the course.
     * @param courseName     in which student are added.
     * @return the list of the students that have been enrolled to che course.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<StudentDTO> enrollAll(List<String> studentSerials, String courseName);

    /**
     * Used to enroll a list of students that is passed by a csv file.
     *
     * @param reader     used to handle the csv file.
     * @param courseName in which student are added.
     * @return the list of the students that have been enrolled to che course.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<StudentDTO> enrollCsv(Reader reader, String courseName);

    /**
     * Used by the student to add an image for the student.
     *
     * @param image updated by the student.
     * @return the student modified.
     */
    @PreAuthorize("hasRole('STUDENT')")
    byte[] uploadImage(byte[] image);

    /**
     * Used by the teacher to remove a student from the course, if allowed.
     *
     * @param studentSerial to identify the student.
     * @param courseName    from which student is removed.
     */
    @PreAuthorize("hasRole('TEACHER')")
    void deleteStudentFromCourse(String studentSerial, String courseName);

}
