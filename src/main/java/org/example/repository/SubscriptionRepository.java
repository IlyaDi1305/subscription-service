package org.example.repository;

import org.example.entity.Subscription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByServiceNameIgnoreCase(String serviceName);

    @Query("SELECT s FROM Subscription s JOIN s.users u GROUP BY s.id ORDER BY COUNT(u.id) DESC")
    List<Subscription> findTopSubscriptions(Pageable pageable);
}
