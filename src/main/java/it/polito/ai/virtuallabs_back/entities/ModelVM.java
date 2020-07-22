package it.polito.ai.virtuallabs_back.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelVM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private byte[] content;
    @OneToOne
    @JoinColumn(name = "course_id")
    private Course course;


    public void setCourse(Course course) {
        if (course != null) {
            this.course = course;
            getCourse().setModelVM(this);
        }
    }
}
