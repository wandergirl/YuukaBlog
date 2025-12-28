package com.blog.Controller;
import com.blog.Service.yuukablogService;
import com.blog.pojo.PageResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.blog.pojo.blog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/blog")
public class yuukablog {
    @Autowired
    yuukablogService blogservice;
    @PostMapping
    public Map<String, Object> insertBlog(@RequestBody blog YuukaBlog){
        blogservice.insertBlog(YuukaBlog);

        // 构造返回结果，确保前端能读到 code
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "发布成功");
        return result;
    }
    @GetMapping
    public PageResult selectBlog(Integer begin, Integer end)
    {
        int pageSize = end - begin;
        return blogservice.selectBlog(begin,pageSize);
    }
    @PutMapping
    public Map<String, Object> update(@RequestBody blog YuukaBlog) {
        blogservice.update(YuukaBlog);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "修改成功");
        return result; // 返回包含 code 的对象
    }
    @DeleteMapping("/{id}")
    void Delte(@PathVariable("id") Integer id)
    {
        blogservice.deleteByid(id);

    }
}
