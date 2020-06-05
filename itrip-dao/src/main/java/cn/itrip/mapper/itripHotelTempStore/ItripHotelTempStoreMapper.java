package cn.itrip.mapper.itripHotelTempStore;
import cn.itrip.beans.pojo.ItripHotelTempStore;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ItripHotelTempStoreMapper {

	public ItripHotelTempStore getItripHotelTempStoreById(@Param(value = "id") Long id)throws Exception;

	public List<ItripHotelTempStore>	getItripHotelTempStoreListByMap(Map<String,Object> param)throws Exception;

	public Integer getItripHotelTempStoreCountByMap(Map<String,Object> param)throws Exception;

	public Integer insertItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception;

	public Integer updateItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception;

	public Integer deleteItripHotelTempStoreById(@Param(value = "id") Long id)throws Exception;
	//熟悉库存（对应房型和日期的记录 无记录则插入记录
    void flushTempStore(Map<String, Object> param)throws Exception;
	//查询并计算剩余库存
	List<ItripHotelTempStore> getItripStoreListByMap(Map<String, Object> param)throws Exception;

    void updateTempStore(Map<String, Object> param)throws Exception;
}
