package com.cloudpocket.repository;

import com.cloudpocket.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByLogin(String login);

    void deleteUserByLogin(String login);

    @Query("SELECT COUNT(u) FROM User u")
    Long countUsers();

}