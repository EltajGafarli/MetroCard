package org.example.abb_interview_task.repository;

import org.example.abb_interview_task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = "update User u set u.isEnabled=false where u.uId = :userId")
    @Modifying
    void deleteUser(@Param("userId") long userId);
}
