package com.example.demo3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties({"password"})
public class User {
    private Integer id;
    private String username;
    private Integer age;

    @JsonIgnore
    private String password;

    private Integer role;

    @JsonIgnore
    private List<Order> orderList;
}