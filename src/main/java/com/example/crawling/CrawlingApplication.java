package com.example.crawling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@EnableTransactionManagement
public class CrawlingApplication implements CommandLineRunner {

//    @Autowired
//    private NewsService newsService;
    @Autowired
    private CrawlImageService crawlImageService;

    public static void main(String[] args) {
        SpringApplication.run(CrawlingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //静态网页爬取
        //爬取指定文本
//        newsService.crawlText();
        //爬取所有图片
        crawlImageService.crawlImage();

    }
}
