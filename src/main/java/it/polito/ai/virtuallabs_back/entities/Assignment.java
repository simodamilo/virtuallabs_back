package it.polito.ai.virtuallabs_back.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Date releaseDate;
    private Date deadline;
    private byte[] content;
    private boolean terminated;

    @ManyToOne
    @JoinColumn(name = "course_name")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_serial")
    private Teacher teacher;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.REMOVE)
    private List<Solution> solutions = new ArrayList<>();

    public void setCourse(Course course) {
        if (this.course != null) this.course.getAssignments().remove(this);
        if (course == null) {
            if (this.course != null) this.course = null;
        } else {
            this.course = course;
            course.getAssignments().add(this);
        }
    }

    public void setTeacher(Teacher teacher) {
        if (this.teacher != null) this.teacher.getAssignments().remove(this);
        if (teacher == null) {
            if (this.teacher != null) this.teacher = null;
        } else {
            this.teacher = teacher;
            teacher.getAssignments().add(this);
        }
    }

    public boolean addSolution(Solution solution) {
        if (solutions.contains(solution)) return false;
        solutions.add(solution);
        solution.setAssignment(this);
        return true;
    }

    public boolean removeSolution(Solution solution) {
        if (!solutions.contains(solution)) return false;
        solutions.remove(solution);
        solution.setAssignment(this);
        return true;
    }

}
