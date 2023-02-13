package com.portal.news.DAO;

import com.portal.news.DTO.*;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PostsDAO {
    @Autowired
    private EntityManager entityManager;
    public ResponseEntity<GetPostDTO> addPost(AddPostDTO addPostDTO) throws IOException{
        Session session = entityManager.unwrap(Session.class);
        if(addPostDTO.getTitle().length() < 5 || addPostDTO.getTitle().length() > 1000){
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
        Files.write(filePath, parts);
        posts.setHrefToText(fileCode);
        session.save(posts);
        return getPost(posts.getId());
    }
    public ResponseEntity<Object> downloadImage(String fileCode) {
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;
        try {
            resource = downloadUtil.getFileAsResource(fileCode, "Images");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        if (resource == null) {
            throw new Failed("File not found");
        }
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
    public ResponseEntity<UploadDTO> uploadImageToPost(MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String filecode = FileUploadUtil.saveFile(fileName, multipartFile, "Images");
        return new ResponseEntity<>(new UploadDTO(filecode), HttpStatus.OK);
    }
    public ResponseEntity<ResultLikeDTO> like(LikeDTO likeDTO){
        Session session = entityManager.unwrap(Session.class);
        Users users = session.get(Users.class, likeDTO.getUser_id());
        if(users == null){
            throw new Failed("Doesnt have such user");
        }
        Posts posts = session.get(Posts.class, likeDTO.getPost_id());
        if(posts == null){
            throw new Failed("Doesnt have such post");
        }
        for(Posts in : users.getLikedPosts()){
            if(in.equals(posts)){
                throw new Failed("Already liked");
            }
        }
        users.addLike(posts);
        posts.setLike((long) posts.getLikes().size());
        session.save(users);
        session.save(posts);
        return new ResponseEntity<>(new ResultLikeDTO(posts.getId(), posts.getLike()), HttpStatus.OK);
    }
    public ResponseEntity<ResultLikeDTO> unlike(LikeDTO likeDTO){
        Session session = entityManager.unwrap(Session.class);
        Users users = session.get(Users.class, likeDTO.getUser_id());
        if(users == null){
            throw new Failed("Doesnt have such user");
        }
        Posts posts = session.get(Posts.class, likeDTO.getPost_id());
        if(posts == null){
            throw new Failed("Doesnt have such post");
        }
        boolean find = false;
        for(Posts in : users.getLikedPosts()){
            if(in.equals(posts)){
                find = true;
            }
        }
        if(!find){
            throw new Failed("No such like");
        }
        users.removeLike(posts);
        posts.setLike((long) posts.getLikes().size());
        session.save(users);
        session.save(posts);
        return new ResponseEntity<>(new ResultLikeDTO(posts.getId(), posts.getLike()), HttpStatus.OK);
    }
    public ResponseEntity<GetPostDTO> getPost(Long id){
        Session session = entityManager.unwrap(Session.class);
        Posts posts = session.get(Posts.class, id);
        if(posts == null){
            throw new Failed("Doesnt have such post");
        }
        String content;
        try {
            content = Files.lines(Paths.get("Posts\\"+posts.getHrefToText()+".txt"))
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e){
            throw new Failed("Invalid post");
        }
        return new ResponseEntity<GetPostDTO>(new GetPostDTO(posts.getId(), posts.getDate(), posts.getLike(),
                posts.getTitle(), posts.getPathToPhoto(), content), HttpStatus.OK);
    }
    public ResponseEntity<HowManyDTO> howManyPosts(){
        Session session = entityManager.unwrap(Session.class);
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_YEAR, -1);
        Date dt2 = new Date(cal2.getTimeInMillis());
        return new ResponseEntity<>(new HowManyDTO((long)session.createQuery("select a from Posts a " +
                        "where date>=:date").setParameter("date", dt2)
                .getResultList().size()), HttpStatus.OK);
    }
    List<Posts> getPostsList(Session session, long from, long howMuch){
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_YEAR, -1);
        Date dt2 = new Date(cal2.getTimeInMillis());
        return session.createQuery("select e from Posts e where e.id >= :first and e.id < :second " +
                        "and e.date >= :date order by e.id desc", Posts.class)
                .setParameter("date", dt2)
                .setParameter("first", from)
                .setParameter("second", from+howMuch)
                .getResultList();
    }
    public ResponseEntity<List<?>> getPosts(Long from, Long howMuch){
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
        while (list.size() != howMuchBefore){
            if(from+howMuch>last){
                throw new Failed("No have so many posts");
            }
            howMuch++;
            list = getPostsList(session, from, howMuch);
        }
        Collections.reverse(list);
        List<Object> result = new ArrayList<>();
        for(Posts in : list){
            String content;
            try {
                content = Files.lines(Paths.get("Posts\\"+in.getHrefToText()+".txt"))
                        .collect(Collectors.joining(System.lineSeparator()));
            } catch (IOException e){
                throw new Failed("No such file");
            }
            result.add(new GetPostDTO(in.getId(), in.getDate(), in.getLike(),
                    in.getTitle(), in.getPathToPhoto(), content));
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
}
