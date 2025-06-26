package com.timesphere.timesphere.service;

import com.timesphere.timesphere.dao.SearchRequest;
import com.timesphere.timesphere.dao.UserSearchDao;
import com.timesphere.timesphere.dto.request.ChangePasswordRequest;
import com.timesphere.timesphere.dto.request.CommentRequest;
import com.timesphere.timesphere.entity.Comment;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.exception.AppException;
import com.timesphere.timesphere.exception.ErrorCode;
import com.timesphere.timesphere.exception.UserNotFoundException;
import com.timesphere.timesphere.repository.CommentRepository;
import com.timesphere.timesphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserSearchDao userSearchDao;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    public void changePassword(ChangePasswordRequest request, Principal connectUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectUser).getPrincipal();

        // the current password not right
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        // the two new password are not the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new AppException(ErrorCode.PASSWORD_CONFIRMATION_MISMATCH);
        }

        // save the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // Tìm người dùng để mời vào team
    public List<UserSuggestionDto> searchUsersForInvitation(String keyword, String teamId) {
        List<User> users = userRepository.searchUsersNotInTeamWithNoPendingInvite(keyword, teamId);
        return users.stream()
                .map(u -> new UserSuggestionDto(
                        u.getId(),
                        u.getFirstname() + " " + u.getLastname(),
                        u.getEmail(),
                        u.getAvatarUrl()))
                .toList();
    }
}
