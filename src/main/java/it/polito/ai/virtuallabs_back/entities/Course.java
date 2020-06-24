package it.polito.ai.virtuallabs_back.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Course {

    @Id
    private String name;
    private int min;
    private int max;
    private boolean enabled;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Team> teams = new ArrayList<>();

    @ManyToMany(mappedBy = "courses")
    private List<Teacher> teachers = new ArrayList<>();

    public boolean addStudent(Student student) {
        if (students.contains(student)) return false;
        students.add(student);
        student.getCourses().add(this);
        return true;
    }

    public boolean removeStudent(Student student) {
        if (!students.contains(student)) return false;
        students.remove(student);
        student.getCourses().remove(this);
        return true;
    }

    public boolean addTeam(Team team) {
        if (teams.contains(team)) return false;
        team.setCourse(this);
        return true;
    }

    public boolean removeTeam(Team team) {
        if (!teams.contains(team)) return false;
        team.setCourse(null);
        return true;
    }

    public boolean addTeacher(Teacher teacher) {
        if (teachers.contains(teacher)) return false;
        teachers.add(teacher);
        teacher.getCourses().add(this);
        return true;
    }

    public boolean removeTeacher(Teacher teacher) {
        if (!teachers.contains(teacher)) return false;
        teachers.remove(teacher);
        teacher.getCourses().remove(this);
        return true;
    }
}
