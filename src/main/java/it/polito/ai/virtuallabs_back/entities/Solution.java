package it.polito.ai.virtuallabs_back.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private byte[] content;
    private State state;
    private Timestamp deliveryTs;
    private boolean modifiable;
    private String grade;
    @ManyToOne
    @JoinColumn(name = "student_serial")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "assignemnt_id")
    private Assignment assignment;

    public void setStudent(Student student) {
        if (this.student != null) this.student.getSolutions().remove(this);
        if (student == null) {
            if (this.student != null) this.student = null;
        } else {
            this.student = student;
            student.getSolutions().add(this);
        }
    }

    public void setAssignment(Assignment assignment) {
        if (this.assignment != null) this.assignment.getSolutions().remove(this);
        if (assignment == null) {
            if (this.assignment != null) this.assignment = null;
        } else {
            this.assignment = assignment;
            assignment.getSolutions().add(this);
        }
    }

    public enum State {
        NULL,
        READ,
        DELIVERED,
        REVIEWED
    }

}
