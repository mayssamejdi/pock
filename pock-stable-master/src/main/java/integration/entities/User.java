package integration.entities;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idUser ;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;
    String email;
    boolean authorized;


}
