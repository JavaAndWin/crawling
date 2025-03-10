package com.example.crawling;

import com.mysql.cj.xdevapi.Result;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;

@Service
@Slf4j
public class CrawlImageService {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    //爬取图片保存到项目文件夹image，并更新数据库path
    @Transactional
    public void crawlImage() throws SQLException, IOException {
        log.info("开始爬取图片");

        //创建一个文件夹保存图片
        File file = new File("images");
        if(!file.exists()){
            file.mkdir();
        }
        //使用JDBC进行持久化,获取连接
        Connection conn = DriverManager.getConnection(url, username, password);
        String insert = "insert into images (path,url,size) values(?,?,?)";
        //获取PreparedStatement对象，Statement因SQL注入风险已淘汰
        PreparedStatement ps = conn.prepareStatement("select size from images where id = '1'");
        ResultSet rs = ps.executeQuery();
        //需要rs.next()返回第一个数据
        int num =0;
        if(rs.next()) {
            num = (int)rs.getFloat("size")+1;
        }
        PreparedStatement preparedStatement = conn.prepareStatement(insert);

        //在数据库中用 id = 0 来记录当前文件编号；

        //爬取图片
        //信任所有证书
        NewsService.disableSslVerification();
        String siteUrl = "https://www.gyrc.cn";
        //获取网页Document对象
        Document doc = Jsoup.connect(siteUrl).get();
        System.out.println(doc);
        //获取Elements对象
        Elements elements = doc.select("img");

        //保存到MySQL
        for (Element element : elements) {
            String imageUrl = siteUrl+"/"+element.attr("src");
            if (!imageUrl.isEmpty()) {
                //生成本地路径
                String path = "images/image_" + num + ".jpg";
                preparedStatement.setString(1, path);
                preparedStatement.setString(2, imageUrl);
                preparedStatement.setFloat(3,downloadImage(imageUrl,"image_" + num + ".jpg")); // 下载图片
                preparedStatement.execute();
                PreparedStatement ps1 = conn.prepareStatement("update images set size = ? where id = '1'");
                ps1.setInt(1,num);
                ps1.execute();
                num++;
            }

        }
        preparedStatement.close();
        log.info("爬取图片完成，一共"+elements.size()+"张");
    }

    //下载并返回文件大小
    private static float downloadImage(String imageUrl,String fileName) throws IOException {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        // 模拟浏览器访问
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        float size =0;


        //获得输出流,try(资源对象){}执行完自动释放资源
        try (InputStream in = connection.getInputStream();
             OutputStream out = new FileOutputStream("images/" + fileName))
        {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                size += bytesRead;
                out.write(buffer, 0, bytesRead);
            }
        }
        System.out.println("保存：" + fileName);
        return size/1024;
    }
}
