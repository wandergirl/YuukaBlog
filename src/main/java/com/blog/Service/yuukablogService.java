package com.blog.Service;
import com.blog.pojo.PageResult;
import com.blog.pojo.blog;
import com.blog.pojo.user;

import java.util.List;
public interface yuukablogService {
    void insertBlog(blog blog);

    PageResult selectBlog(Integer begin, Integer pageSize);

    void update(blog YuukaBlog);

    void deleteByid(Integer id);

    user login(user u);
}
