package cn.itrip.mapper.itripAreaDic;
import cn.itrip.beans.pojo.ItripAreaDic;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ItripAreaDicMapper {

	public ItripAreaDic getItripAreaDicById(@Param(value = "id") Long id)throws Exception;

	public List<ItripAreaDic>	getItripAreaDicListByMap(Map<String,Object> param)throws Exception;

	public Integer getItripAreaDicCountByMap(Map<String,Object> param)throws Exception;

	public Integer insertItripAreaDic(ItripAreaDic itripAreaDic)throws Exception;

	public Integer updateItripAreaDic(ItripAreaDic itripAreaDic)throws Exception;

	public Integer deleteItripAreaDicById(@Param(value = "id") Long id)throws Exception;

	//根据酒店id查询商圈列表

    List<ItripAreaDic> getItripAreaDicListByHotelId(Long hotelId);
}
