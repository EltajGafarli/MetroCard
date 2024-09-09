package org.example.abb_interview_task.repository;

import org.example.abb_interview_task.entity.Role;
import org.example.abb_interview_task.entity.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByRoleEnum(RoleEnum role);
}