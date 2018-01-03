package com.blibli.future.project1.model;

import javax.persistence.*;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Entity // Menandakan bahwa class ini merupakan suatu entitas
@Table(name = "app_user") // Menandakan bahwa class ini akan dibuat menjadi tabel dengan nama app_user
public class User implements Serializable {

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom dengan nama user_id, dan merupakan primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom user_name, dan tidak boleh kosong
     */
    @Column(name = "user_name", nullable = false)
    private String userName;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom user_email, dan kolom ini unik serta tidak boleh kosong
     */
    @Column(name = "user_email", unique = true, nullable = false)
    private String userEmail;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom user_password, dan kolom ini tidak boleh kosong
     */
    @Column(name = "user_password", nullable = false)
    private String userPassword;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom user_phone, dan kolom ini tidak boleh kosong
     */
    @Column(name = "user_phone", nullable = false)
    private String userPhone;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom user_address, dan kolom ini tidak boleh kososng
     */
    @Column(name = "user_address", nullable = false)
    private String userAddress;

    /**
     * Menandakan bahwa tabel ini memiliki relasi dengan tabel role, dengan foreign key role_id
     * Relasinya adalah many to one
     * many di user one di role --> satu role bisa memiliki banyak user
     */
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static String passwordEncoder(String userPassword) throws NoSuchAlgorithmException {
        StringBuilder userPasswordEncode = new StringBuilder();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(userPassword.getBytes());

        byte userPasswordByte[] = messageDigest.digest();

        for (int i = 0; i < userPasswordByte.length; i++) {
            userPasswordEncode.append(Integer.toString((userPasswordByte[i] & 0xff) + 0x100, 16).substring(1));
        }

        return userPasswordEncode.toString();
    }

    @Override
    public String toString() {
        return userName;
    }
}
