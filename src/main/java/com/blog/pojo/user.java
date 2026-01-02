package com.blog.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class user {
   private int id;
    private String username;
    private String password;
    private String role;
    private String avatarUrl;
    private String email;


}
