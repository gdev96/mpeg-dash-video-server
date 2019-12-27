package com.unict.dieei.pr20.videomanagementservice.repository;

import com.unict.dieei.pr20.videomanagementservice.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
