package it.polito.ai.virtuallabs_back.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ModelVM {

    @Id
    private String id;
    private String name;

    @OneToMany(mappedBy = "modelVM")
    private List<Team> teams = new ArrayList<>();

    public boolean addTeam(Team team) {
        if (teams.contains(team)) return false;
        team.setModelVM(this);
        return true;
    }

    public boolean removeTeam(Team team) {
        if (!teams.contains(team)) return false;
        team.setModelVM(null);
        return true;
    }
}
