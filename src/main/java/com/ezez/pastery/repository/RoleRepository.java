package com.ezez.pastery.repository;

import com.ezez.pastery.model.role.Role;
import com.ezez.pastery.model.role.RoleName;
import org.hibernate.sql.Insert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(RoleName name);
}
