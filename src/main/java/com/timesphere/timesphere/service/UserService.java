package com.timesphere.timesphere.service;

import com.timesphere.timesphere.dao.SearchRequest;
import com.timesphere.timesphere.dao.UserSearchDao;
import com.timesphere.timesphere.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserSearchDao userSearchDao;

    public List<User> searchUsers(SearchRequest request) {
        return userSearchDao.findAllByCriteria(request);
    }
}
