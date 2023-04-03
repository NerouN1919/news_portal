package com.portal.news.DAO;

import com.portal.news.DTO.*;
import com.portal.news.DataBase.Comments;
import com.portal.news.DataBase.Posts;
import com.portal.news.DataBase.Users;
import com.portal.news.Errors.Failed;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CommentsDAO {
    @Autowired
    private EntityManager entityManager;
    public void addComment(AddCommentDTO addCommentDTO) throws IOException { //Добавление комментария к посту
        Session session = entityManager.unwrap(Session.class);
        Posts posts = session.get(Posts.class, addCommentDTO.getPostId());
        Users users = session.get(Users.class, addCommentDTO.getUserId());
        if(posts == null || users == null){ //Проверка на наличие поста и пользователя
            throw new Failed("No such post or user");
        }
        String fileCode = RandomStringUtils.randomAlphanumeric(25);
        List<String> parts = Arrays.asList(addCommentDTO.getComment().split("\n"));
        Path uploadPath = Paths.get("Comments");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileCode+".txt");
        Files.write(filePath, parts); //Создание и запись в файл текст комментария
        Comments comments = new Comments(fileCode);
        posts.addComment(comments, users);
        session.save(posts);
        session.save(comments);
    }
    public ResponseEntity<HowManyDTO> howManyComments(Long id){ //Получение инормации о том, сколько комментарив
        Session session = entityManager.unwrap(Session.class);
        Posts posts = session.get(Posts.class, id);
        if (posts == null){ //Проверка на наличие поста
            throw new Failed("Doesnt have such post");
        }
        return new ResponseEntity<>(new HowManyDTO((long) posts.getComments().size()), HttpStatus.OK);
    }
    private List<Comments> getListForGetComments(Session session, Long from, Long howMuch, Long postId){ //Запрос для получения списка комментариев из бд
        return session.createQuery("select e from Comments e where e.id between :first and :second " +
                        "and e.post = :third", Comments.class)
                .setParameter("first", from-howMuch)
                .setParameter("second", from)
                .setParameter("third", session.get(Posts.class, postId))
                .getResultList();
    }
    public ResponseEntity<List<?>> getComments(Long from, Long howMuch, Long postId){ //Получение списка комментариев
        if(from < 0){ //Проверка на корректность id
            throw new Failed("Bad from id");
        }
        if(howMuch < 0){ //Проверка на корректность числа комментариев
            throw new Failed("Bad howMuch");
        }
        Session session = entityManager.unwrap(Session.class);
        if(session.get(Posts.class, postId) == null){
            throw new Failed("No such post"); //Проверка на наличие поста
        }
        if(from == 0){
            from = session.createQuery("select a from Comments a order by a.id desc ", Comments.class)
                    .setMaxResults(1).getResultList().get(0).getId();
        }
        List<Comments> list = getListForGetComments(session, from, howMuch-1, postId);
        long beforeHowMuch = howMuch;
        while(list.size()!=beforeHowMuch){ //Проверка на пропусти в базе данных
            howMuch++;
            list = getListForGetComments(session, from, howMuch, postId);
        }
        Collections.reverse(list);
        List<Object> result = new ArrayList<>();
        for(Comments in: list){
            StringBuilder content = new StringBuilder();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader("./Comments/" +
                        in.getHrefToComment() + ".txt"));
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    content.append(line).append("\n");
                }

            } catch (IOException e){
                throw new Failed("No such file"); //Проверка на наличие файла
            }
            result.add(new ReturnedCommentDTO(content.toString(), in.getPost().getId(), in.getUser().getId(), in.getDate()));
        }
        try {
            result.add(new IdForNextDTO(session.createQuery("select a from Comments a where a.id < :first and " +
                                    "a.post=:second",
                            Comments.class).setParameter("first", list.get(list.size()-1).getId())
                    .setParameter("second", session.get(Posts.class, postId))
                    .setMaxResults(1).getResultList().get(0).getId()));
        } catch (IndexOutOfBoundsException e){
            result.add(new IdForNextDTO(null));
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    public void deleteComment(Long id){
        Session session = entityManager.unwrap(Session.class);
        Comments comments = session.get(Comments.class, id);
        if (comments == null){
            throw new Failed("Bad comment id");
        }
        Posts posts = comments.getPost();
        posts.deleteComment(comments);
        File file = new File("./Comments/" + comments.getHrefToComment() + ".txt");
        file.delete();
        session.delete(comments);
    }
}
