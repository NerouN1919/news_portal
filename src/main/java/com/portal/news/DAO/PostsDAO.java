package com.portal.news.DAO;

import com.portal.news.DTO.*;
import com.portal.news.DataBase.Comments;
import com.portal.news.DataBase.Posts;
import com.portal.news.DataBase.Users;
import com.portal.news.Errors.Failed;
import com.portal.news.FileWork.FileDownloadUtil;
import com.portal.news.FileWork.FileUploadUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
public class PostsDAO {
    @Autowired
    private EntityManager entityManager;
    public ResponseEntity<GetPostDTO> addPost(AddPostDTO addPostDTO) throws IOException{ //Добавление нового поста
        Session session = entityManager.unwrap(Session.class);
        if(addPostDTO.getTitle().length() < 5 || addPostDTO.getTitle().length() > 1000){ //Проверка на корректность введённых данных
            throw new Failed("Wrong length of title");
        }
        Posts posts = new Posts(addPostDTO.getImagePath(), addPostDTO.getTitle());
        String fileCode = RandomStringUtils.randomAlphanumeric(25);
        List<String> parts = Arrays.asList(addPostDTO.getText().split("\n"));
        Path uploadPath = Paths.get("Posts");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileCode+".txt");
        Files.write(filePath, parts); //Создание файла и добавление туда текст поста
        posts.setHrefToText(fileCode);
        session.save(posts);
        return new ResponseEntity<>(new GetPostDTO(posts.getId(), posts.getDate(), posts.getLike(), posts.getTitle(),
                posts.getPathToPhoto(), addPostDTO.getText(), null), HttpStatus.OK);
    }
    public ResponseEntity<Object> downloadImage(String fileCode) { //Получение изображения
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;
        try {
            resource = downloadUtil.getFileAsResource(fileCode, "Images"); //получение изображения из хранилища
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        if (resource == null) { //Проверка на существование такого файла
            throw new Failed("File not found");
        }
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(Base64.getEncoder().encodeToString(Files.readAllBytes(resource.getFile().toPath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public ResponseEntity<UploadDTO> uploadImageToPost(MultipartFile multipartFile) throws IOException { //Загрузка изображения
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String filecode = FileUploadUtil.saveFile(fileName, multipartFile, "Images");
        return new ResponseEntity<>(new UploadDTO(filecode), HttpStatus.OK);
    }
    public ResponseEntity<ResultLikeDTO> like(LikeDTO likeDTO){ //Поставить лайк посту
        Session session = entityManager.unwrap(Session.class);
        Users users = session.get(Users.class, likeDTO.getUser_id());
        if(users == null){ //Проверка на наличие пользователя
            throw new Failed("Doesnt have such user");
        }
        Posts posts = session.get(Posts.class, likeDTO.getPost_id());
        if(posts == null){ //Проверка на наличие поста
            throw new Failed("Doesnt have such post");
        }
        for(Posts in : users.getLikedPosts()){
            if(in.equals(posts)){ //Проверка на то, поставлен ли уже лайк
                throw new Failed("Already liked");
            }
        }
        users.addLike(posts);
        posts.setLike((long) posts.getLikes().size());
        session.save(users);
        session.save(posts);
        return new ResponseEntity<>(new ResultLikeDTO(posts.getId(), posts.getLike()), HttpStatus.OK);
    }
    public ResponseEntity<ResultLikeDTO> unlike(LikeDTO likeDTO){ //Убрать лайк с поста
        Session session = entityManager.unwrap(Session.class);
        Users users = session.get(Users.class, likeDTO.getUser_id());
        if(users == null){ //Проверка на наличие пользователя
            throw new Failed("Doesnt have such user");
        }
        Posts posts = session.get(Posts.class, likeDTO.getPost_id());
        if(posts == null){ //Проверка на наличие поста
            throw new Failed("Doesnt have such post");
        }
        boolean find = false;
        for (Posts in : users.getLikedPosts()){
            if (in.equals(posts)) {
                find = true;
                break;
            }
        }
        if(!find){ //Проверка на наличие лайка
            throw new Failed("No such like");
        }
        users.removeLike(posts);
        posts.setLike((long) posts.getLikes().size());
        session.save(users);
        session.save(posts);
        return new ResponseEntity<>(new ResultLikeDTO(posts.getId(), posts.getLike()), HttpStatus.OK);
    }
    public ResponseEntity<GetPostDTO> getPost(Long post_id, Long user_id){ //Получение информации о посте
        Session session = entityManager.unwrap(Session.class);
        Posts posts = session.get(Posts.class, post_id);
        if(posts == null){ //Проверка на наличие поста
            throw new Failed("Doesnt have such post");
        }
        StringBuilder content = new StringBuilder();
        boolean isLiked = false;
        for(Users in: posts.getLikes()){
            if (in.getId().equals(user_id)) {
                isLiked = true;
                break;
            }
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader("./Posts/" + posts.getHrefToText() + ".txt"));
            String line;
            while((line = in.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e){ //Проверка на корректность информации о посте
            System.out.println(e.getMessage());
            throw new Failed("Invalid post");
        }
        return new ResponseEntity<GetPostDTO>(new GetPostDTO(posts.getId(), posts.getDate(), posts.getLike(),
                posts.getTitle(), posts.getPathToPhoto(), content.toString(), isLiked), HttpStatus.OK);
    }
    public ResponseEntity<HowManyDTO> howManyPosts(){ //Получение количества постов
        Session session = entityManager.unwrap(Session.class);
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_YEAR, -1); //Получение даты прошлого дня
        Date dt2 = new Date(cal2.getTimeInMillis());
        return new ResponseEntity<>(new HowManyDTO((long)session.createQuery("select a from Posts a " +
                        "where date>=:date").setParameter("date", dt2)
                .getResultList().size()), HttpStatus.OK);
    }
    List<Posts> getPostsList(Session session, long from, long howMuch){ //Запрос для получения списка постов
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_YEAR, -1); //Получение даты прошлого дня
        Date dt2 = new Date(cal2.getTimeInMillis());
        return session.createQuery("select e from Posts e where e.id >= :first and e.id < :second " +
                        "and e.date >= :date order by e.id desc", Posts.class)
                .setParameter("date", dt2)
                .setParameter("first", from)
                .setParameter("second", from+howMuch)
                .getResultList();
    }
    public ResponseEntity<List<?>> getPosts(Long from, Long howMuch, Long user_id){ //Получение списка постов
        if(from < 0){
            throw new Failed("Bad from id");
        }
        Session session = entityManager.unwrap(Session.class);
        List<Posts> list = getPostsList(session, from, howMuch);
        long howMuchBefore = howMuch;
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_YEAR, -1);
        Date dt2 = new Date(cal2.getTimeInMillis());
        long last = session.createQuery("select a from Posts a where a.date >= :date order by a.id desc",
                        Posts.class)
                .setParameter("date", dt2)
                .setMaxResults(1).getResultList().get(0).getId();
        while (list.size() != howMuchBefore){ //Проверка на то, пропущены ли посты в базе данных
            if(from+howMuch>last){ //Проверка хватает ли постов в базе данных
                throw new Failed("No have so many posts");
            }
            howMuch++;
            list = getPostsList(session, from, howMuch);
        }
        Collections.reverse(list);
        List<Object> result = new ArrayList<>();
        for(Posts in : list){
            StringBuilder content = new StringBuilder();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader("./Posts/" + in.getHrefToText() + ".txt"));
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            } catch (IOException e){ //Проверка на наличие файла
                throw new Failed("No such file");
            }
            boolean isLiked = false;
            for(Users user: in.getLikes()) {
                if (user.getId().equals(user_id)) {
                    isLiked = true;
                    break;
                }
            }
            result.add(new GetPostDTO(in.getId(), in.getDate(), in.getLike(),
                    in.getTitle(), in.getPathToPhoto(), content.toString(), isLiked));
        }
        try{
            result.add(new IdForNextDTO(session.createQuery("select a from Posts a where a.id > :first",
                            Posts.class).setParameter("first", list.get(list.size()-1).getId()).setMaxResults(1).
                    getResultList().get(0).getId()));
        } catch (IndexOutOfBoundsException e){
            result.add(new IdForNextDTO(null));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    public void deletePost(Long id){
        Session session = entityManager.unwrap(Session.class);
        Posts post = session.get(Posts.class, id);
        if (post == null){
            throw new Failed("Bad post id");
        }
        File file = new File("./Posts/" + post.getHrefToText() + ".txt");
        file.delete();
        file = new File("./Images/" + post.getPathToPhoto() + ".jpg");
        file.delete();
        List<Comments> list = post.getComments();
        for(Comments in: list){
            File commentFile = new File("./Comments/" + in.getHrefToComment() + ".txt");
            commentFile.delete();
        }
        session.delete(post);
    }
    public void updatePost(EditPostDTO editPostDTO){
        Session session = entityManager.unwrap(Session.class);
        Posts post = session.get(Posts.class, editPostDTO.getId());
        Path filePath = Paths.get("Posts/" + post.getHrefToText() + ".txt");
        List<String> parts = Arrays.asList(editPostDTO.getText().split("\n"));
        try {
            Files.write(filePath, parts); //Создание файла и добавление туда текст поста
        } catch (IOException e) {
            throw new Failed("Bad file");
        }
    }
}
