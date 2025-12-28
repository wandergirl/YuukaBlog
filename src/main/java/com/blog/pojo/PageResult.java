package com.blog.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult {
    private Long total;     // 总记录数
    private List<blog> rows;   // 当前页数据列表
}
