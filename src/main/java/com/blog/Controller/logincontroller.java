package com.blog.Controller;
import com.blog.Service.yuukablogService;
import com.blog.pojo.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.blog.util.JwtUtils;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/login")
public class logincontroller {
    @Autowired
    yuukablogService blogservice;
    @PostMapping
    public Map<String, Object> login(@RequestBody user u) {
        Map<String, Object> result = new HashMap<>();
        user yuuka = blogservice.login(u);

        if (yuuka != null) {
            // 生成令牌时传入唯一标识
            String token = JwtUtils.createToken(yuuka.getUsername());

            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("token", token);
            result.put("user", yuuka);

            // ⭐ 必须返回 result 才能让前端拿到 code 和 token
            return result;
        } else {
            result.put("code", 401);
            result.put("message", "邮箱或密码错误");
            return result;
        }
    }
}
