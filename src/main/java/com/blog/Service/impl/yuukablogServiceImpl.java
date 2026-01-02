package com.blog.Service.impl;
import com.blog.Service.yuukablogService;
import com.blog.mapper.yuukablogmapper;
import com.blog.pojo.PageResult;
import com.blog.pojo.blog;
import com.blog.pojo.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class yuukablogServiceImpl implements yuukablogService{
    @Autowired
    yuukablogmapper yuukamapper;
    @Override
    public void insertBlog(blog blog) {
        yuukamapper.insertBlog(blog);
    }

    @Override
    public PageResult selectBlog(Integer begin, Integer pageSize) {
        List<blog> rows = yuukamapper.selectBlog(begin, pageSize);
        // 2. 调用 mapper 查询总条数
        Long total = yuukamapper.selectCount();
        return new PageResult(total,rows);
    }

    @Override
    public void update(blog YuukaBlog) {

        yuukamapper.updateByid(YuukaBlog);
    }

    @Override
    public void deleteByid(Integer id) {
        yuukamapper.deleteByid(id);
    }

    @Override
    public user login(user u) {
        user yuuka= yuukamapper.login(u);
        if(yuuka!=null)
        {
            return yuuka;
        }
        return null;
    }
}
