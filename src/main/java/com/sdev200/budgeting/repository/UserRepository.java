package com.sdev200.budgeting.repository;

import com.sdev200.budgeting.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);
}
