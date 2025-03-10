package com.example.crawling;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.sql.Date;

@Service
@Slf4j
public class NewsService {
    private final NewsRepository newsRepository;

    @Autowired
    public NewsService(NewsRepository newsRepository) {  // ✅ 构造器注入
        this.newsRepository = newsRepository;
    }

    //爬取文本
    public void crawlText() throws IOException {
        log.info("开始爬取文本");
        disableSslVerification();
        String url = "https://www.tfjy.gov.cn/info/iList.jsp?cat_id=14138";
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select("li.ewb-data-list");
        for (Element element : elements) {
            //提取元素
            System.out.println(element.text());
            String strDate = element.select(".ewb-data-date").text();
            String title = element.select(".ewb-data-infor a").text();

            System.out.println(strDate);
            Date date = Date.valueOf(strDate);
            //通过实体类映射保存到数据库
            News news = new News();
            news.setTitle(title);
            news.setNewsdate(date);
            newsRepository.save(news);
        }

    }



    public static void disableSslVerification() {
        try {
            // 创建一个信任所有证书的TrustManager
            TrustManager[] trustAllCertificates = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            // 设置SSL上下文，使用上述的TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustAllCertificates, new java.security.SecureRandom());
            // 设置全局的SSLContext
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            // 忽略SSL的Host验证
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
