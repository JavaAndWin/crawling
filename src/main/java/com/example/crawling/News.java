package com.example.crawling;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Table(name = "news")
@Data
public class News {
    //JPA映射对象，以实体类成员属性映射数据库字段
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Date newsdate;
}
