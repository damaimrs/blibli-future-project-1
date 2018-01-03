package com.blibli.future.project1.repository;

import com.blibli.future.project1.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Menandakan bahwa class ini merupakan sebuah repository
public interface UserRepository extends JpaRepository<User, Integer> {

    public Long countAllByRole_RoleId(Integer roleId);

    /**
     * Mendefinisikan method untuk mengambil user berdasarkan user_email
     *
     * @param userEmail merupakan user email dari user tersebut
     * @return adalah akun usernya
     */
    public User findUserByUserEmail(String userEmail);

    public List<User> findUsersByRole_RoleId(Integer roleId);
    public Page<User> findUsersByRole_RoleId(Integer roleId, Pageable pageable);

    @Query("select u from User u where " +
            "u.userName like %:searchText% or " +
            "u.userEmail like %:searchText% or " +
            "u.userPhone like %:searchText% or " +
            "u.userAddress like %:searchText%")
    public Page<User> searchUsers(@Param("searchText") String searchText, Pageable pageable);

    @Query("select u from User u where " +
            "u.role.roleId = :roleId and " +
            "(" +
            "u.userName like %:searchText% or " +
            "u.userEmail like %:searchText% or " +
            "u.userPhone like %:searchText% or " +
            "u.userAddress like %:searchText% " +
            ")")
    public Page<User> searchUsers(@Param("searchText") String searchText, @Param("roleId") Integer roleId,
                                  Pageable pageable);
}
