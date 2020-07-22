package it.polito.ai.virtuallabs_back.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamToken {

    @Id
    private String id;
    private Long teamId;
    private String studentSerial;
    private Timestamp expiryDate;
    private int status;
}
