package com.blog.mapper;
import com.blog.pojo.Result;
import com.blog.pojo.cover;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface yuukaCoverMapper {

    void insert_url(cover yuukacover);

    @Select("SELECT url FROM cover ORDER BY RAND() LIMIT 1")
    String get_url(Integer id);
}
