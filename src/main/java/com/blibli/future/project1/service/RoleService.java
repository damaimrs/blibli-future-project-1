package com.blibli.future.project1.service;

import com.blibli.future.project1.model.Role;
import com.blibli.future.project1.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional // Menandakan bahwa service ini menerapkan transaksi
@Service // Menandakan bahwa class ini merupakan service
public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Page<Role> findAll(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public Page<Role> searchRoles(String searchText, Pageable pageable) {
        return roleRepository.searchRoles(searchText, pageable);
    }

    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    public Role findRoleById(Integer roleId) {
        return roleRepository.findOne(roleId);
    }

    public Long getRoleCount() {
        return roleRepository.count();
    }

    public void deleteRole(Integer roleId) {
        roleRepository.delete(roleId);
    }
}
