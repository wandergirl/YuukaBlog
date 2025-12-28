package com.blog.pojo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class blog {

    private Integer id;
    private String title;
    private String content;

    @JsonProperty("category_id")
    private Integer categoryId;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("update_time")
    private String updateTime;
}
