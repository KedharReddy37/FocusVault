package com.focusvault.auth_service.repository;

import com.focusvault.auth_service.entity.BrowsingEvent;
import com.focusvault.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BrowsingEventRepository extends JpaRepository<BrowsingEvent, Long> {

    List<BrowsingEvent> findByUserOrderByStartTimeDesc(User user);
}