package it.polito.ai.virtuallabs_back.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class VM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int vcpu;
    private int disk;
    private int ram;
    private boolean on;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "vm_student", joinColumns = @JoinColumn(name = "wm_id"),
            inverseJoinColumns = @JoinColumn(name = "student_serial"))
    private List<Student> owners = new ArrayList<>();


    public void setTeam(Team team) {
        if (this.team != null) this.team.getVms().remove(this);
        if (team == null) {
            if (this.team != null) this.team = null;
        } else {
            this.team = team;
            team.getVms().add(this);
        }
    }

    public boolean addOwner(Student student) {
        if (owners.contains(student)) return false;
        owners.add(student);
        student.getVms().add(this);
        return true;
    }

    public boolean removeOwner(Student student) {
        if (!owners.contains(student)) return false;
        owners.remove(student);
        student.getVms().remove(this);
        return true;
    }

}
