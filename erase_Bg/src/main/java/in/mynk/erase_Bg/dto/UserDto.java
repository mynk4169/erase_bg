package in.mynk.erase_Bg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {


    private String email;
    private String firstName;
    private String lastName;
    private String clerkId;
    private Integer credits;
    private String photoUrl;
}
