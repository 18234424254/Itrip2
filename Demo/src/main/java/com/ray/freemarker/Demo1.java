package com.ray.freemarker;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo1 {
    public static void main(String[] args) throws IOException, TemplateException {
        //创建一个配置对象，配置环境
        Configuration conf = new Configuration(Configuration.getVersion());
        //设置模板所在的位置
        conf.setDirectoryForTemplateLoading(new File(Thread.currentThread().getContextClassLoader().getResource("").getPath()));
        //获取模板文件对象
        Template template = conf.getTemplate("hello.ftl");
        //
        User user = new User();
        user.setName("jack");
        user.setPassword("222");
        User user1 = new User();
        user1.setName("joon");
        user1.setPassword("111");
        User user2 = new User();
        user2.setName("jarray");
        user2.setPassword("333");
        User user3 = new User();
        user3.setName("");
        user3.setPassword("444");

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);




        //创建数据
        Map<String,Object> map = new HashMap<>();
        map.put("user",user);
        map.put("name","tom123");
        map.put("list",userList);
       // map.put("field","name");
        //合成-生成目标文档
        template.process(map,new FileWriter(new File("hello.html")));
    }
}
