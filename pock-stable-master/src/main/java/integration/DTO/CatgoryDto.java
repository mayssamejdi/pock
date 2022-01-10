package integration.DTO;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CatgoryDto {


    long id;
    String name;

    Timestamp updateddate;

    Timestamp creationdate;

}
