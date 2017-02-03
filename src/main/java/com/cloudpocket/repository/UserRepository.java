package com.cloudpocket.repository;

import com.cloudpocket.model.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByLogin(String login);

    void deleteUserByLogin(String login);

    long count();

    Page<User> findAll(Pageable pageable);

    List<User> findByAdmin(boolean isAdmin);

}