package com.unict.dieei.pr20.videomanagementservice.repository;

import com.unict.dieei.pr20.videomanagementservice.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
