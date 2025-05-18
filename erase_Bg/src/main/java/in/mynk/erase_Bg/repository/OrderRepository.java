package in.mynk.erase_Bg.repository;

import in.mynk.erase_Bg.entity.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<OrderEntity, String> {
    Optional<OrderEntity> findByOrderId(String orderId);
}
