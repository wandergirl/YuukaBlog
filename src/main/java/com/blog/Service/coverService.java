package com.blog.Service;
import com.blog.pojo.Result;
import com.blog.pojo.cover;
import org.springframework.stereotype.Service;

public interface coverService {
    void save_url(cover yuukacover);

    Result get_url(Integer id);
}
