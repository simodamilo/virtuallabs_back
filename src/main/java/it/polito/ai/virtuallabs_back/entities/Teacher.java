package it.polito.ai.virtuallabs_back.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Teacher {

    @Id
    private String id;
    private String name;
    private String firstName;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "teacher_course", joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "course_name"))
    private List<Course> courses = new ArrayList<>();

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
}
