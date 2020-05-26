package cn.itrip.search.service;

import cn.itrip.beans.vo.ItripHotelVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.Page;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.List;

public interface ItripSearchService {
    Page<ItripHotelVO> getHotelListByPage(SearchHotelVO searchHotelVO) throws Exception;
    List<ItripHotelVO> getItripHotelListByCity(Integer cityId, Integer count)throws Exception;
}
