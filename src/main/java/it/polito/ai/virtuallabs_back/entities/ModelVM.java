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
    @OneToOne
    @JoinColumn(name = "team_id")
    private Team team;


    public void setTeam(Team team) {
        if (team != null) {
            this.team = team;
            getTeam().setModelVM(this);
        }
    }
}
