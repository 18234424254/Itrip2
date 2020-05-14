package com.ray.freemarker;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.awt.image.DirectColorModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo2 {
    public static void main(String[] args) throws Exception {
        //JDBC
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/itripdb", "root", "1234");
        //获取数据库中标结构信息
        DatabaseMetaData metaData = conn.getMetaData();
        //获取普通的数据表
        ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
        //遍历结果集
        while (tables.next()){
            String table_name = tables.getString("TABLE_NAME");
            System.out.println("表名："+table_name);
            //获取当前表中的所有列信息
            ResultSet columns = metaData.getColumns(null, "%", table_name, "%");
            //遍历所有的列
            while (columns.next()){
                //获取列名
                String column_name = columns.getString("COLUMN_NAME");
                //获取列的类型
                String type_name = columns.getString("TYPE_NAME");
                //获取字段备注信息Comment
                String remarks = columns.getString("REMARKS");

                System.out.println("\t"+column_name+","+type_name+","+remarks);
            }
        }
        conn.close();


    }
}
