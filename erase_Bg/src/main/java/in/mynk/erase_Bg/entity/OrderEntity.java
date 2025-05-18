package in.mynk.erase_Bg.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "tbl_orders")
public class OrderEntity {

    @Id
    private String id;  // MongoDB id

    @Indexed(unique = true)
    private String orderId;  // unique

    private String clerkId;
    private String plan;
    private Double amount;
    private Integer credits;
    private Boolean payment;

    @CreatedDate
    private Instant createdAt;

    // Default values handled in builder or service layer
    public void prePersist(){
        if (payment == null) {
            payment = false;
        }
    }
}
