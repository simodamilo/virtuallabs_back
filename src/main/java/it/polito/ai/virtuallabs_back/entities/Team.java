package it.polito.ai.virtuallabs_back.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int status;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "team_student", joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> members = new ArrayList<>();

    public void setCourse(Course course) {
        if (this.course != null) this.course.getTeams().remove(this);   //rimovibile se orphanRemoval == true in Course
        if (course == null) {
            if (this.course != null) this.course = null;
        } else {
            this.course = course;
            course.getTeams().add(this);
        }
    }

    public boolean addMember(Student student) {
        if (members.contains(student)) return false;
        members.add(student);
        student.getTeams().add(this);
        return true;
    }

    public boolean removeMember(Student student) {
        if (!members.contains(student)) return false;
        members.remove(student);
        student.getTeams().remove(this);
        return true;
    }
}
