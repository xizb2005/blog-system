package com.example.demo3.entity;
import lombok.Data;
@Data
public class Order {
    private Integer id;
    private String orderName;
    private Double price;
    private Integer userId;
}