package com.blog.Service.impl;
import com.blog.Service.coverService;
import com.blog.mapper.yuukaCoverMapper;
import com.blog.pojo.Result;
import com.blog.pojo.cover;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class coverServiceImpl implements coverService {
    @Autowired yuukaCoverMapper coverMapper;
    @Override
    public void save_url(cover yuukacover) {
        coverMapper.insert_url(yuukacover);
    }

    @Override
    public Result get_url(Integer id) {

        return Result.success(coverMapper.get_url(id));
    }
}
