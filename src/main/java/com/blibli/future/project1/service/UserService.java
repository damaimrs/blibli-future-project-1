package com.blibli.future.project1.service;

import com.blibli.future.project1.model.User;
import com.blibli.future.project1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional // Menandakan bahwa service ini menerapkan transaksi
@Service // Menandakan bahwa class ini merupakan service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllByRoleId(Integer roleId) {
        return userRepository.findUsersByRole_RoleId(roleId);
    }

    public Page<User> findAllByRoleId(Integer roleId, Pageable pageable) {
        return userRepository.findUsersByRole_RoleId(roleId, pageable);
    }

    public User findUserById(Integer id) {
        return userRepository.findOne(id);
    }

    public User findUserByEmail(String userEmail) {
        return userRepository.findUserByUserEmail(userEmail);
    }

    public Long getUserCount() {
        return userRepository.count();
    }

    public Long getUserCountByRoleId(Integer roleId) {
        return userRepository.countAllByRole_RoleId(roleId);
    }

    public Page<User> findAllPageable(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> searchUsers(String searchText, Pageable pageable) {
        return userRepository.searchUsers(searchText, pageable);
    }

    public Page<User> searchUsers(String searchText, Integer roleId, Pageable pageable) {
        return userRepository.searchUsers(searchText, roleId, pageable);
    }

    public void deleteUser(Integer userId) {
        userRepository.delete(userId);
    }
}
