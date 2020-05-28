package cn.itrip.search.service;

import cn.itrip.beans.vo.ItripHotelVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.Page;
import cn.itrip.search.dao.BaseQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
public class ItripSearchServiceImpl implements ItripSearchService {
    @Resource
    private BaseQuery baseQuery;

    @Override
    public Page<ItripHotelVO> getHotelListByPage(SearchHotelVO searchHotelVO) throws IOException, SolrServerException {
        //处理查询参数
        SolrQuery query = new SolrQuery("*:*");
        StringBuffer sb = new StringBuffer("destination:"+searchHotelVO.getDestination());
        String keywords = searchHotelVO.getKeywords();

        if (keywords!=null) {
            String replace = keywords.replace(" ", "|");
            sb.append(" AND keyword:"+replace);
        }
        query.setQuery(sb.toString());
        //处理价格
        StringBuffer sb2= new StringBuffer();
        Double maxPrice = searchHotelVO.getMaxPrice();
        if (maxPrice!=null) {
            query.addFilterQuery("minPrice:[* TO "+maxPrice+"]");
        }

        //450以上
        Double minPrice = searchHotelVO.getMinPrice();
        if (minPrice!=null) {
            query.addFilterQuery("maxPrice:["+minPrice+" TO *]");
        }


        //处理商圈
        //前段参数 3619,3620  tradingAreaIds
        StringBuffer sb3 = new StringBuffer();
        String tradingAreaIds = searchHotelVO.getTradeAreaIds();
        if (tradingAreaIds!=null){
            String[] split = tradingAreaIds.split(",");
            sb3.append("tradingAreaIds:(");
            for (int i = 0; i < split.length;i++) {
                if (i==0){
                    sb3.append("*"+split[i]+"*");
                }else {
                    sb3.append(" OR *"+split[i]+"*");
                }
            }
            sb3.append(")");
            System.out.println("sb3======"+sb3.toString());
            query.addFilterQuery(sb3.toString());
        }

        //酒店星级 数字1-5 执行hotelLevel
        Integer hotelLevel = searchHotelVO.getHotelLevel();
        if (hotelLevel!=null){
            query.addFilterQuery("hotelLevel:"+hotelLevel);
        }

        //处理酒店特色
        StringBuffer sb4 = new StringBuffer();
        String featureIds = searchHotelVO.getFeatureIds();
        if (featureIds!=null){
            String[] split = featureIds.split(",");
            sb4.append("featureIds:(");
            for (int i=0; i < split.length;i++) {
                if (i==0){
                    sb4.append("*"+split[i]+"*");
                }else {
                    sb4.append(" OR *"+split[i]+"*");
                }
            }
            sb4.append(")");
            query.addFilterQuery(sb4.toString());
        }


        //调用dao执行查询
        Page<ItripHotelVO> page=baseQuery.query(query,searchHotelVO.getPageNo(),searchHotelVO.getPageSize(),ItripHotelVO.class);
        //返回数据
        return page;
    }

    @Override
    public List<ItripHotelVO> getItripHotelListByCity(Integer cityId, Integer count) throws Exception {
        SolrQuery query = new SolrQuery("*:*");
        query.setQuery("cityId:"+cityId);
        Page<ItripHotelVO> page = baseQuery.query(query, 1, count, ItripHotelVO.class);
        List<ItripHotelVO> rows = page.getRows();
        return rows;
    }

}
