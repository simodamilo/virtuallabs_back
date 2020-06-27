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
public class UserToken {

    @Id
    private String id;
    private Long userId;
    private Timestamp expiryDate;
    private String name;
    private String surname;
}
