package com.blibli.future.project1.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity // Menandakan bahwa class ini merupakan sebuah entitas
@Table(name = "role") // Menandakan bahwa class ini akan dibuat tabel dengan nama role
public class Role implements Serializable {

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom dengan nama role_id, dan merupakan primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int roleId;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom dengan nama role_name, dan kolom ini unik dan tidak boleh kososng
     */
    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }
}
