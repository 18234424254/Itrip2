package cn.itrip.search.dao;

import cn.itrip.beans.vo.ItripHotelVO;
import cn.itrip.common.Constants;
import cn.itrip.common.Page;
import cn.itrip.common.PropertiesUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class BaseQuery<T> {
    private HttpSolrClient client;
    public BaseQuery(){
        //创建sql客户端对象
         client = new HttpSolrClient.Builder()
                 .withBaseSolrUrl(PropertiesUtils.get("database.properties","baseUrl"))
                 .withConnectionTimeout(10000)
                 .withSocketTimeout(600000)
                 .build();
    }

    public Page<T> query(SolrQuery query, Integer pageNo, Integer pageSize,Class<T> clazz) throws IOException, SolrServerException {
        pageSize=pageSize==null?Constants.DEFAULT_PAGE_SIZE:pageSize;
        pageNo=pageNo==null? Constants.DEFAULT_PAGE_NO :pageNo;
        int beginPos=(pageNo-1)*pageSize;

        query.setStart(beginPos);
        query.setRows(pageSize);

        QueryResponse response = client.query(query);
        List<T> beans = response.getBeans(clazz);
        long numFound = response.getResults().getNumFound();
        Page page = new Page(pageNo,pageSize,(int)numFound);
        page.setRows(beans);
        page.setBeginPos(beginPos);
        return page;
    }
}
