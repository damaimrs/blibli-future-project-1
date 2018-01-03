package com.blibli.future.project1;

import com.blibli.future.project1.model.Role;
import com.blibli.future.project1.model.User;
import com.blibli.future.project1.service.RoleService;
import com.blibli.future.project1.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Project1Application implements CommandLineRunner {

    /**
     * Object untuk menuliskan log
     */
    private final static Logger logger = LoggerFactory.getLogger(Project1Application.class);

    /**
     * Object untuk melakukan akses ke database
     */
    private RoleService roleService;
    private UserService userService;

    @Autowired
    public Project1Application(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Project1Application.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        setAdmin();
    }

    /*
     * Menginputkan akun awal admin ke database
     */
    private void setAdmin() {
        try {
            Role role = new Role();
            User user = new User();

            role.setRoleName("Admin");
            role = roleService.addRole(role);

            user.setUserName("admin");
            user.setUserEmail("admin@blibli.com");
            user.setUserPassword(User.passwordEncoder("admin"));
            user.setUserPhone("");
            user.setUserAddress("");
            user.setRole(role);

            userService.addUser(user);
        } catch (Exception e) {
            logger.error("Admin was created");
        }
    }
}
