package in.mynk.erase_Bg.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "tbl_users")
public class UserEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String clerkId;

    @Indexed(unique = true)
    private String email;

    private String firstName;
    private String lastName;
    private Integer credits;
    private String photoUrl;



}
