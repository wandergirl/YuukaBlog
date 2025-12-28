package com.blog.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class user {
    int id;
    String username;
    String password;
    String role;
    private String avatarUrl;

}
