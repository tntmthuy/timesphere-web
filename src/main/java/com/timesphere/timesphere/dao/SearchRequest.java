package com.timesphere.timesphere.dao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String username;
}
