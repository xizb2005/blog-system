package com.example.demo3.common.page;
import lombok.Data;
import java.util.List;

@Data
public class PageInfo<T> {
    // 当前页码
    private Integer pageNum;
    // 每页条数
    private Integer pageSize;
    // 数据总条数
    private Long total;
    // 总页数
    private Integer pages;
    // 当前页数据集合
    private List<T> list;
}