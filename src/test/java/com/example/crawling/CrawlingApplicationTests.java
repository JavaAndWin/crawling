package com.example.crawling;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;

@SpringBootTest
class CrawlingApplicationTests {

    @Autowired
    private NewsRepository newsRepository;
    @Test
    void contextLoads() {
        News news = new News();
        news.setTitle("title");
        Date date = Date.valueOf("2025-03-07");
        news.setNewsdate(date);
        newsRepository.save(news);
    }

}
