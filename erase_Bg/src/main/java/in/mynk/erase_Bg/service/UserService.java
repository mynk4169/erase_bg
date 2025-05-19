package in.mynk.erase_Bg.service;

import in.mynk.erase_Bg.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByClerkId(String clerkId);
    User createUser(String clerkId, String email, String firstName, String lastName);
    User updateUser(String clerkId, String email, String firstName, String lastName);
    User updateCredits(String clerkId, int credits);
    User updatePlan(String clerkId, String plan);
    void deactivateUser(String clerkId);
}
