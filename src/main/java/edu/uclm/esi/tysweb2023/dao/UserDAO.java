package edu.uclm.esi.tysweb2023.dao;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;

import edu.uclm.esi.tysweb2023.model.User;
import org.springframework.stereotype.Component;

public interface UserDAO extends CrudRepository<User, String> {

    User findByEmailAndPwd(String email, String pwd);
}
