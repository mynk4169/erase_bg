package in.mynk.erase_Bg.repository;

import in.mynk.erase_Bg.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByClerkId(String clerkId);
    Optional<User> findByEmail(String email);
    boolean existsByClerkId(String clerkId);
    boolean existsByEmail(String email);
}
