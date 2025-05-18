package in.mynk.erase_Bg.repository;

import in.mynk.erase_Bg.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, String> {
    boolean existsByClerkId(String clerkId);
    Optional<UserEntity> findByClerkId(String clerkId);
}
