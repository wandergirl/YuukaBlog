package com.blog.mapper;
import com.blog.pojo.blog;
import com.blog.pojo.user;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface yuukablogmapper {
    void insertBlog(blog Yuukablog);

    List<blog> selectBlog( @Param("begin") Integer begin, @Param("end") Integer end);

    void updateByid(blog id);
    @Delete("DELETE FROM yuukablog WHERE id = #{id}")
    void deleteByid(Integer id);
    @Select("select count(*) from yuukablog")
    Long selectCount();
    user login(user u);
}
