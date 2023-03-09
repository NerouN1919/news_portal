package com.portal.news.DAO;


import javax.persistence.EntityManager;

import com.portal.news.DTO.IdDTO;
import com.portal.news.DTO.LoginDTO;
import com.portal.news.DTO.RegDTO;
import com.portal.news.DTO.UserInfoDTO;
import com.portal.news.DataBase.Users;
import com.portal.news.Errors.Failed;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

@Repository
public class UserDAO {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public ResponseEntity<IdDTO> registration(RegDTO regDTO){ //Регистрация пользователя
        Session session = entityManager.unwrap(Session.class);
        if(!Pattern.matches("^[A-Z][a-zA-Z]+$", regDTO.getName())){ //Проверка на корректность имени
            throw new Failed("Bad name");
        }
        if(!Pattern.matches("^[A-Z][a-zA-Z]+$", regDTO.getSurname())){ //Проверка на корректность фаилии
            throw new Failed("Bad surname");
        }
        if(!Pattern.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", regDTO.getEmail())){
            throw new Failed("Bad email"); //Проверка на корректость email
        }
        List<Users> isIn = session.createQuery("select a from Users a where a.email=:email",
                Users.class).setParameter("email", regDTO.getEmail()).getResultList();
        if(isIn.size()==0){ //Проверка на существование уже такого email
            Users users = new Users(regDTO.getName(), regDTO.getSurname(),
                    passwordEncoder.encode(regDTO.getPasswordHash()),regDTO.getEmail());
            session.save(users);
            return new ResponseEntity<>(new IdDTO(users.getId()), HttpStatus.OK);
        }
        throw new Failed("Exists this email");
    }
    public ResponseEntity<IdDTO> login(LoginDTO loginDTO){ //Вход в аккаунт
        Session session = entityManager.unwrap(Session.class);
        List<Users> need = session.createQuery("select a from Users a where a.email=:email",
                Users.class).setParameter("email", loginDTO.getEmail()).getResultList();
        if(need.size()==0){ //Проверка на существование такого email
            throw new Failed("Dont have such email");
        }
        if(passwordEncoder.matches(loginDTO.getPassword(), need.get(0).getPasswordHash())){ //Проверка на совпадение паролей
            return new ResponseEntity<>(new IdDTO(need.get(0).getId()), HttpStatus.OK);
        }
        throw new Failed("Wrong password");
    }
    public ResponseEntity<UserInfoDTO> getUserInfo(Long id){ //Получение информации о пользователе
        Session session = entityManager.unwrap(Session.class);
        Users need = session.get(Users.class, id);
        if(need==null){ //Проверка на существование такого пользователя
            throw new Failed("No such id");
        }
        return new ResponseEntity<>(new UserInfoDTO(need.getId(), need.getEmail(), need.getName(), need.getSurname()),
                HttpStatus.OK);
    }
}
