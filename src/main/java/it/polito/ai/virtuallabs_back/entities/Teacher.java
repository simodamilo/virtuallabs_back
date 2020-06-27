package it.polito.ai.virtuallabs_back.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {

    @Id
    private String serial;
    @Column(unique = true)
    private String email;
    private String name;
    private String surname;
    private Byte[] image;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "teacher_course", joinColumns = @JoinColumn(name = "teacher_serial"),
            inverseJoinColumns = @JoinColumn(name = "course_name"))
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "teacher")
    private List<Assignment> assignments = new ArrayList<>();

    public boolean addCourse(Course course) {
        if (courses.contains(course)) return false;
        courses.add(course);
        course.getTeachers().add(this);
        return true;
    }

    public boolean removeCourse(Course course) {
        if (!courses.contains(course)) return false;
        courses.remove(course);
        course.getTeachers().remove(this);
        return true;
    }

    public boolean addAssignment(Assignment assignment) {
        if (assignments.contains(assignment)) return false;
        assignments.add(assignment);
        assignment.setTeacher(this);
        return true;
    }

    public boolean removeAssignment(Assignment assignment) {
        if (!assignments.contains(assignment)) return false;
        assignments.remove(assignment);
        assignment.setTeacher(this);
        return true;
    }
}
