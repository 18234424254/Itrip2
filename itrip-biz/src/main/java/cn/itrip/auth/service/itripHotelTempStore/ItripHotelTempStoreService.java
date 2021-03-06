package cn.itrip.auth.service.itripHotelTempStore;
import cn.itrip.beans.pojo.ItripHotelTempStore;
import java.util.List;
import java.util.Map;

import cn.itrip.beans.vo.order.RoomStoreVO;
import cn.itrip.beans.vo.order.ValidateRoomStoreVO;
import cn.itrip.common.Page;
/**
* Created by shang-pc on 2015/11/7.
*/
public interface ItripHotelTempStoreService {

    public ItripHotelTempStore getItripHotelTempStoreById(Long id)throws Exception;

    public List<ItripHotelTempStore>	getItripHotelTempStoreListByMap(Map<String,Object> param)throws Exception;

    public Integer getItripHotelTempStoreCountByMap(Map<String,Object> param)throws Exception;

    public Integer itriptxAddItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception;

    public Integer itriptxModifyItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception;

    public Integer itriptxDeleteItripHotelTempStoreById(Long id)throws Exception;

    public Page<ItripHotelTempStore> queryItripHotelTempStorePageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception;
    //查询指定区间可预订的剩余库存
    List<ItripHotelTempStore> getItripStoreListByMap(Map<String, Object> param)throws Exception;
    //下单成功后 更新实时库存
    void updateTempStore(Map<String, Object> param)throws Exception;
}
