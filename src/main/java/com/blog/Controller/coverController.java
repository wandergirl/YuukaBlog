package com.blog.Controller;
import com.blog.Service.coverService;
import com.blog.pojo.Result;
import com.blog.pojo.cover;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/cover")
public class coverController {
    @Autowired coverService coverservice;
    @PostMapping // 必须明确是 Post 请求
    public Map<String, String> uploader(@RequestPart cover Yuukacover ,@RequestPart("file") MultipartFile file) throws IOException {

        // 1. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String uuidName = UUID.randomUUID().toString();
        String finalName = uuidName + originalFilename;

        // 2. 确保目录存在
        File dir = new File("/root/poster/");
        if (!dir.exists()) dir.mkdirs();

        // 3. 保存文件
        file.transferTo(new File(dir, finalName));

        // 4. 构建返回结果 (必须是 Map/对象，这样返回的就是 JSON {"url": "..."})
        Map<String, String> result = new HashMap<>();
        Yuukacover.url = "http://yuukablog.xyz/poster/" + finalName;

        coverservice.save_url(Yuukacover);

        return result;
    }

    @GetMapping("{id}")
    public Result get_url(@PathVariable("id") Integer id){
        Result result= coverservice.get_url(id);
        return Result.success(result);
    }

}
