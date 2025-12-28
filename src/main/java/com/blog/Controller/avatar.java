package com.blog.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/avatar")
public class avatar {
    @PostMapping // 必须明确是 Post 请求
    public Map<String, String> uploader(@RequestParam("file") MultipartFile file) throws IOException {

        // 1. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String uuidName = UUID.randomUUID().toString();
        String finalName = uuidName + originalFilename;

        // 2. 确保目录存在
        File dir = new File("D:/UploadData/");
        if (!dir.exists()) dir.mkdirs();

        // 3. 保存文件
        file.transferTo(new File(dir, finalName));

        // 4. 构建返回结果 (必须是 Map/对象，这样返回的就是 JSON {"url": "..."})
        Map<String, String> result = new HashMap<>();
        String url = "http://localhost:8080/avatar/" + finalName;
        result.put("url", url);

        System.out.println("上传成功，访问路径: " + url);
        return result;
    }
}
