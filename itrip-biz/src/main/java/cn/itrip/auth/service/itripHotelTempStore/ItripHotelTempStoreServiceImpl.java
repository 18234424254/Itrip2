package cn.itrip.auth.service.itripHotelTempStore;
import cn.itrip.beans.vo.order.RoomStoreVO;
import cn.itrip.beans.vo.order.ValidateRoomStoreVO;
import cn.itrip.mapper.itripHotelTempStore.ItripHotelTempStoreMapper;
import cn.itrip.beans.pojo.ItripHotelTempStore;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import cn.itrip.common.Constants;
@Service
public class ItripHotelTempStoreServiceImpl implements ItripHotelTempStoreService {

    @Resource
    private ItripHotelTempStoreMapper itripHotelTempStoreMapper;

    public ItripHotelTempStore getItripHotelTempStoreById(Long id)throws Exception{
        return itripHotelTempStoreMapper.getItripHotelTempStoreById(id);
    }

    public List<ItripHotelTempStore>	getItripHotelTempStoreListByMap(Map<String,Object> param)throws Exception{
        return itripHotelTempStoreMapper.getItripHotelTempStoreListByMap(param);
    }

    public Integer getItripHotelTempStoreCountByMap(Map<String,Object> param)throws Exception{
        return itripHotelTempStoreMapper.getItripHotelTempStoreCountByMap(param);
    }

    public Integer itriptxAddItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception{
            itripHotelTempStore.setCreationDate(new Date());
            return itripHotelTempStoreMapper.insertItripHotelTempStore(itripHotelTempStore);
    }

    public Integer itriptxModifyItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception{
        itripHotelTempStore.setModifyDate(new Date());
        return itripHotelTempStoreMapper.updateItripHotelTempStore(itripHotelTempStore);
    }

    public Integer itriptxDeleteItripHotelTempStoreById(Long id)throws Exception{
        return itripHotelTempStoreMapper.deleteItripHotelTempStoreById(id);
    }

    public Page<ItripHotelTempStore> queryItripHotelTempStorePageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception{
        Integer total = itripHotelTempStoreMapper.getItripHotelTempStoreCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripHotelTempStore> itripHotelTempStoreList = itripHotelTempStoreMapper.getItripHotelTempStoreListByMap(param);
        page.setRows(itripHotelTempStoreList);
        return page;
    }

    /**
     * 查询可预订剩余库存
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public List<ItripHotelTempStore> getItripStoreListByMap(Map<String, Object> param) throws Exception {
        //刷新库存 （把实时库存表中没记录的查询日期进行插入记录
        itripHotelTempStoreMapper.flushTempStore(param);

        //查询并计算
        List<ItripHotelTempStore> storeList = itripHotelTempStoreMapper.getItripStoreListByMap(param);



        return storeList;
    }

    @Override
    public void updateTempStore(Map<String, Object> param) throws Exception {

        itripHotelTempStoreMapper.updateTempStore(param);
    }


}
