package com.ray.freemarker;


import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo3 {
    public static void main(String[] args) throws Exception {
        //JDBC
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/itripdb", "root", "1234");
        //获取数据库中标结构信息
        DatabaseMetaData metaData = conn.getMetaData();
        //获取普通的数据表
        //ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
        //遍历结果集
//        while (tables.next()){
//            String table_name = tables.getString("TABLE_NAME");
//            System.out.println("表名："+table_name);
            //获取当前表中的所有列信息
            ResultSet columns = metaData.getColumns(null, "%", "itrip_user", "%");

            //封装表数据
            Table table = new Table();
            table.setTableName("itrip_user");
            List<Column> cols = new ArrayList<>();

             Column col;
            //遍历所有的列
            while (columns.next()){

                col = new Column();
                //获取列名
                String column_name = columns.getString("COLUMN_NAME");
                col.setColumnName(column_name);
                //获取列的类型
                String type_name = columns.getString("TYPE_NAME");
                col.setColumnType(type_name);
                //获取字段备注信息Comment
                String remarks = columns.getString("REMARKS");
                col.setComment(remarks);
                cols.add(col);
            }
            table.setColumns(cols);

       // }
        conn.close();
        //模板数据
        Map<String,Object> data = new HashMap<>();
        data.put("table",table);
        //创建摸版
        Configuration conf = new Configuration(Configuration.getVersion());
        conf.setDirectoryForTemplateLoading(new File(Thread.currentThread().getContextClassLoader().getResource("").getPath()));
        Template template = conf.getTemplate("Model.ftl");
        //组合数据和模板生成目标文件
        template.process(data,new FileWriter(table.getTableName()+".java"));


    }
}
