package it.polito.ai.virtuallabs_back.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Student {

    @Id
    private String id;
    private String name;
    private String firstName;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_course", joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_name"))
    private List<Course> courses = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<Team> teams = new ArrayList<>();

    public boolean addCourse(Course course) {
        if (courses.contains(course)) return false;
        courses.add(course);
        course.getStudents().add(this);
        return true;
    }

    public boolean removeCourse(Course course) {
        if (!courses.contains(course)) return false;
        courses.remove(course);
        course.getStudents().remove(this);
        return true;
    }

    public boolean addTeam(Team team) {
        if (teams.contains(team)) return false;
        teams.add(team);
        team.getMembers().add(this);
        return true;
    }

    public boolean removeTeam(Team team) {
        if (!teams.contains(team)) return false;
        teams.remove(team);
        team.getMembers().remove(this);
        return true;
    }
}
