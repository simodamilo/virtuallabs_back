package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface CourseService {

    /**
     * With the addCourse method a new course is added and the teacher who insert it is considered
     * the principal teacher. He/She has the possibility to add other teachers by using the
     * addTeacherToCourse method.
     *
     * @param courseDTO it is the course that must be added.
     * @return it returns the inserted course.
     */
    @PreAuthorize("hasRole('TEACHER')")
    CourseDTO addCourse(CourseDTO courseDTO);

    /**
     * With the getCourse method all details about the specific course are returned.
     *
     * @param name it is the name that must be searched.
     * @return it returns the searched course, it must be empty.
     */
    Optional<CourseDTO> getCourse(String name);

    /**
     * With the getAllCourses method all courses with all details are returned.
     *
     * @return it returns the list of all courses in the application.
     */
    List<CourseDTO> getAllCourses();

    /**
     * With the getEnrolledStudents method all students enrolled to the specific course are returned.
     *
     * @param courseName it is the course for which the user want the enrolled students.
     * @return list of enrolled students.
     */
    List<StudentDTO> getEnrolledStudents(String courseName);

    /**
     * With the getTeamsForCourse method all teams of the specific course are returned.
     *
     * @param courseName it is the course for which the user want the list of teams.
     * @return list of all teams inside the course.
     */
    List<TeamDTO> getTeamsForCourse(String courseName);

    /**
     * If the course exists and the teacher has the permissions, the course is modified with data
     * received from the client. All fields are updated, even if they are not changed.
     *
     * @param courseDTO it is the course with new data.
     * @return the new courseDTO.
     */
    @PreAuthorize("hasRole('TEACHER')")
    CourseDTO modifyCourse(CourseDTO courseDTO);

    /**
     * With the addStudentToCourse method a student is enrolled to a specific course.
     *
     * @param studentId  it is the serial of the student that must be enrolled.
     * @param courseName it is the name of the course in which the student must be enrolled.
     * @return the enrolled studentDTO is returned, if it is not possible the studentDTO is null.
     */
    @PreAuthorize("hasRole('TEACHER')")
    StudentDTO addStudentToCourse(String studentId, String courseName);

    /**
     * With the addTeacherToCourse method a teacher is added to a specific course by the chief teacher.
     *
     * @param teacherId  it is the serial of the teacher that must be added to the course.
     * @param courseName it is the course in which the teacher be must be added.
     * @return it returns the teacherDTO if all is ok, otherwise it returns null.
     */
    @PreAuthorize("hasRole('TEACHER')")
    TeacherDTO addTeacherToCourse(String teacherId, String courseName);

    /**
     * With the enrollAll method all students are enrolled to the specific course,
     * it invokes the addStudentToCourse method for each student in the list.
     *
     * @param studentIds it is a list of student serials that must be added to the course.
     * @param courseName it is the name of the course in which students must be enrolled.
     * @return it returns a list of studentDTOs, if a problem occurs for one student,
     * null will be inserted inside the list.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<StudentDTO> enrollAll(List<String> studentIds, String courseName);

    /**
     * With the enrollAll method all students are enrolled to the specific course,
     * it invokes the enrollAll by passing a list of student serials and the course name.
     *
     * @param r          it is used to take all the student from the csv file.
     * @param courseName it is the name of the course in which students must be enrolled.
     * @return it returns a list of studentDTOs, if a problem occurs for one student,
     * null will be inserted inside the list.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<StudentDTO> enrollCsv(Reader r, String courseName);

    /**
     * With the deleteCourse method the passed course is removed if exists and if the current teacher is one
     * of the teachers of the courses. All the relationship are also deleted, for this reason a copy of the
     * teachers list is taken in order to delete them from the course.
     *
     * @param name it is the name of the course that must be deleted.
     */
    @PreAuthorize("hasRole('TEACHER')")
    void deleteCourse(String name);

    /**
     * If the course exists and the teacher has the permissions, the student is deleted
     * from the course.
     *
     * @param studentId  it is the serial of the student that must be deleted from the course.
     * @param courseName it is the name of the course from which student must be enrolled.
     */
    @PreAuthorize("hasRole('TEACHER')")
    void deleteStudentFromCourse(String studentId, String courseName);

    /**
     * This method is used to get all the courses of the authenticated teacher. In order to perform it a
     * custom query was created in courseRepository.
     *
     * @return it returns all the courses of the authenticated teacher.
     */
    @PreAuthorize("hasRole('TEACHER')")
    List<CourseDTO> getTeacherCourses();

}
