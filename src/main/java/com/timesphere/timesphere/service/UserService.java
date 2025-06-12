package com.timesphere.timesphere.service;

import com.timesphere.timesphere.dao.SearchRequest;
import com.timesphere.timesphere.dao.UserSearchDao;
import com.timesphere.timesphere.dto.CommentRequest;
import com.timesphere.timesphere.entity.Comment;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.exception.UserNotFoundException;
import com.timesphere.timesphere.repository.CommentRepository;
import com.timesphere.timesphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserSearchDao userSearchDao;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public List<User> searchUsers(SearchRequest request) {
        return userSearchDao.findAllByCriteria(request);
    }

    //test
    public String postComment(CommentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .cmt_create_at(LocalDateTime.now())
                .cmt_by(user)
                .build();

        commentRepository.save(comment);
        return formatComment(comment);
    }

    private String formatComment(Comment comment) {
        return String.format("Comment content <<%s>> from: %s",
                comment.getContent(),
                comment.getCmt_by().getFirstname().toUpperCase());
    }



    //devteria
//    public User createUser(UserCreationRequest request){
//        User user = new User();
//
//        user.setEmail(request.getEmail());
//        user.setPassword(request.getPassword());
//        user.setFirstname(request.getFirst_name());
//        user.setLastname(request.getLast_name());
//
//        return userRepository.save(user);
//    }
//
//    public User updateUser(String userId, UserUpdateRequest request){
//        User user = getUser(userId);
//
//        user.setPassword(request.getPassword());
//        user.setFirstname(request.getFirst_name());
//        user.setLastname(request.getLast_name());
//
//        return userRepository.save(user);
//    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }
}
