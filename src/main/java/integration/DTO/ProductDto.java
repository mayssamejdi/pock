package integration.DTO;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
public class ProductDto {

    private Long id;

    private String name;

    private boolean disponibility;


    private Timestamp createddate;


    private Timestamp updatedte;

    private String quantity;
}
