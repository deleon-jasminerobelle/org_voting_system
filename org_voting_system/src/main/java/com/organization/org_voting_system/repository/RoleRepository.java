package com.organization.org_voting_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.organization.org_voting_system.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(Role.RoleName roleName);

    default Role findByName(String name) {
        return findByRoleName(Role.RoleName.valueOf(name)).orElse(null);
    }

    @Modifying
    @Transactional
    @Query(value = "UPDATE roles SET role_name = :newName WHERE role_name = :oldName", nativeQuery = true)
    void updateRoleName(@Param("oldName") String oldName, @Param("newName") String newName);

}
