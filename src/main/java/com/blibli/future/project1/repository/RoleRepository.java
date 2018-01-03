package com.blibli.future.project1.repository;

import com.blibli.future.project1.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository // Menandakan bahwa class ini merupakan sebuah repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("select r from Role r where r.roleName like %:searchText%")
    public Page<Role> searchRoles(@Param("searchText") String searchText, Pageable pageable);
}
