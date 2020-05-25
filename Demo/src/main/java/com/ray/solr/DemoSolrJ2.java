package com.ray.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;



import java.io.IOException;
import java.util.List;

public class DemoSolrJ2 {
    public static void main(String[] args) throws IOException, SolrServerException {
        //创建solr的http请求客户端对象
        HttpSolrClient client = new HttpSolrClient.Builder("http://localhost:8080/solr").build();
        //串案查询参数
        SolrQuery query = new SolrQuery("*:*");
        query.addFilterQuery("keyword:北京");
        query.setRows(5);
        query.setStart(2);
        query.setSort("id",SolrQuery.ORDER.desc);

        //执行查询获得相应结果
        QueryResponse response = client.query("core1", query, SolrRequest.METHOD.GET);
        //从响应中获得数据
        List<Hotel> list = response.getBeans(Hotel.class);
        for (Hotel hotel : list) {
            System.out.println(hotel);
        }
    }
}
