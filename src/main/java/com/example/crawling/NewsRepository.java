package com.example.crawling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    @Override
    <S extends News> S save(S entity);

}
