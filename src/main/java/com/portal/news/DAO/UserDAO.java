package com.portal.news.DAO;


import javax.persistence.EntityManager;

import com.portal.news.DTO.IdDTO;
import com.portal.news.DTO.LoginDTO;
import com.portal.news.DTO.RegDTO;
import com.portal.news.DTO.UserInfoDTO;
import com.portal.news.DataBase.Users;
import com.portal.news.Errors.Failed;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAO {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public ResponseEntity<IdDTO> registartion(@NotNull RegDTO regDTO){
        Session session = entityManager.unwrap(Session.class);
        List<Users> isIn = session.createQuery("from Users where email='"+regDTO.getEmail()+"'",
                Users.class).getResultList();
        if(isIn.size()==0){
            Users users = new Users(regDTO.getName(), regDTO.getSurname(),
                    passwordEncoder.encode(regDTO.getPasswordHash()),regDTO.getEmail());
            session.save(users);
            return new ResponseEntity<>(new IdDTO(users.getId()), HttpStatus.OK);
        }
        throw new Failed("Exists this email");
    }
    public ResponseEntity<IdDTO> login(@NotNull LoginDTO loginDTO){
        Session session = entityManager.unwrap(Session.class);
        List<Users> need = session.createQuery("from Users where email='"+loginDTO.getEmail()+"'",
                Users.class).getResultList();
        if(need.size()==0){
            throw new Failed("Dont have such email");
        }
        if(passwordEncoder.matches(loginDTO.getPassword(), need.get(0).getPasswordHash())){
            return new ResponseEntity<>(new IdDTO(need.get(0).getId()), HttpStatus.OK);
        }
        throw new Failed("Wrong password");
    }
    public ResponseEntity<UserInfoDTO> getUserInfo(Long id){
        Session session = entityManager.unwrap(Session.class);
        Users need = session.get(Users.class, id);
        if(need==null){
            throw new Failed("No such id");
        }
        return new ResponseEntity<>(new UserInfoDTO(need.getId(), need.getEmail(), need.getName(), need.getSurname()),
                HttpStatus.OK);
    }
}
