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
    private int vcpu;
    private int disk;
    private int ram;
    private int activeInstance;
    private int maxInstance;

    @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
    private List<VM> vms;

    @ManyToOne
    @JoinColumn(name = "course_name")
    private Course course;

    @ManyToMany()
    @JoinTable(name = "team_student", joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "student_serial"))
    private List<Student> members = new ArrayList<>();


    public boolean addVm(VM vm) {
        if (vms.contains(vm)) return false;
        vm.setTeam(this);
        return true;
    }

    public boolean removeVm(VM vm) {
        if (!vms.contains(vm)) return false;
        vm.setTeam(null);
        return true;
    }

    public void setCourse(Course course) {
        if (this.course != null) this.course.getTeams().remove(this);
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
