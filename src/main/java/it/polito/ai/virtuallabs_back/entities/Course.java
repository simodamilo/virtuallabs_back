package it.polito.ai.virtuallabs_back.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Course {

    @Id
    private String name;
    private String tag;
    private int min;
    private int max;
    private boolean enabled;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<VM> vms = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Assignment> assignments = new ArrayList<>();

    @ManyToMany(mappedBy = "courses")
    private List<Teacher> teachers = new ArrayList<>();

    @OneToOne(mappedBy = "course")
    private ModelVM modelVM;

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

    public boolean addVm(VM vm) {
        if (vms.contains(vm)) return false;
        vms.add(vm);
        vm.setCourse(this);
        return true;
    }

    public boolean removeVm(VM vm) {
        if (!vms.contains(vm)) return false;
        vms.remove(vm);
        vm.setCourse(this);
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

    public boolean addAssignment(Assignment assignment) {
        if (assignments.contains(assignment)) return false;
        assignments.add(assignment);
        assignment.setCourse(this);
        return true;
    }

    public boolean removeAssignment(Assignment assignment) {
        if (!assignments.contains(assignment)) return false;
        assignments.remove(assignment);
        assignment.setCourse(this);
        return true;
    }

    /* vedere se ha senso mettere la possibilità di eliminare il modelVM dal team */
    public void setModelVM(ModelVM modelVM) {
        if (modelVM != null) {
            this.modelVM = modelVM;
            getModelVM().setCourse(this);
        }
    }
}
