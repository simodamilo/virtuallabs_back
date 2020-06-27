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
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    private String serial;
    @Column(unique = true)
    private String email;
    private String name;
    private String surname;
    private Byte[] image;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_course", joinColumns = @JoinColumn(name = "student_serial"),
            inverseJoinColumns = @JoinColumn(name = "course_name"))
    private List<Course> courses = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<Team> teams = new ArrayList<>();

    @ManyToMany(mappedBy = "owners")
    private List<VM> vms = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Solution> solutions = new ArrayList<>();

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

    public boolean addVm(VM vm) {
        if (vms.contains(vm)) return false;
        vms.add(vm);
        vm.getOwners().add(this);
        return true;
    }

    public boolean removeVm(VM vm) {
        if (!vms.contains(vm)) return false;
        vms.remove(vm);
        vm.getOwners().remove(this);
        return true;
    }

    public boolean addSolution(Solution solution) {
        if (solutions.contains(solution)) return false;
        solutions.add(solution);
        solution.setStudent(this);
        return true;
    }

    public boolean removeSolution(Solution solution) {
        if (!solutions.contains(solution)) return false;
        solutions.remove(solution);
        solution.setStudent(this);
        return true;
    }
}
