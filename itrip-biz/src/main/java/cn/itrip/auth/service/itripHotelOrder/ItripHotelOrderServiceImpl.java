package cn.itrip.auth.service.itripHotelOrder;
import cn.itrip.auth.service.itripHotel.ItripHotelService;
import cn.itrip.auth.service.itripHotelRoom.ItripHotelRoomService;
import cn.itrip.auth.service.itripHotelTempStore.ItripHotelTempStoreService;
import cn.itrip.beans.pojo.ItripHotel;
import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.beans.pojo.ItripHotelTempStore;
import cn.itrip.beans.vo.order.RoomStoreVO;
import cn.itrip.beans.vo.order.ValidateRoomStoreVO;
import cn.itrip.mapper.itripHotelOrder.ItripHotelOrderMapper;
import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.itrip.common.Constants;
@Service
public class ItripHotelOrderServiceImpl implements ItripHotelOrderService {

    @Resource
    private ItripHotelTempStoreService itripHotelTempStoreService;

    @Resource
    private ItripHotelOrderMapper itripHotelOrderMapper;

    @Resource
    private ItripHotelService itripHotelService;

    @Resource
    private ItripHotelRoomService itripHotelRoomService;

    public ItripHotelOrder getItripHotelOrderById(Long id)throws Exception{
        return itripHotelOrderMapper.getItripHotelOrderById(id);
    }

    public List<ItripHotelOrder>	getItripHotelOrderListByMap(Map<String,Object> param)throws Exception{
        return itripHotelOrderMapper.getItripHotelOrderListByMap(param);
    }

    public Integer getItripHotelOrderCountByMap(Map<String,Object> param)throws Exception{
        return itripHotelOrderMapper.getItripHotelOrderCountByMap(param);
    }

    public Integer itriptxAddItripHotelOrder(ItripHotelOrder itripHotelOrder)throws Exception{
            itripHotelOrder.setCreationDate(new Date());
            return itripHotelOrderMapper.insertItripHotelOrder(itripHotelOrder);
    }

    public Integer itriptxModifyItripHotelOrder(ItripHotelOrder itripHotelOrder)throws Exception{
        itripHotelOrder.setModifyDate(new Date());
        return itripHotelOrderMapper.updateItripHotelOrder(itripHotelOrder);
    }

    public Integer itriptxDeleteItripHotelOrderById(Long id)throws Exception{
        return itripHotelOrderMapper.deleteItripHotelOrderById(id);
    }

    public Page<ItripHotelOrder> queryItripHotelOrderPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception{
        Integer total = itripHotelOrderMapper.getItripHotelOrderCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripHotelOrder> itripHotelOrderList = itripHotelOrderMapper.getItripHotelOrderListByMap(param);
        page.setRows(itripHotelOrderList);
        return page;
    }

    /**
     * 获取订单预定信息
     * @param storeVO
     * @return
     * @throws Exception
     */
    @Override
    public RoomStoreVO getPreOrderInfo(ValidateRoomStoreVO storeVO) throws Exception {
        RoomStoreVO roomStoreVO =new RoomStoreVO();
        Long hotelId = storeVO.getHotelId();
        Long roomId = storeVO.getRoomId();
        Date checkInDate = storeVO.getCheckInDate();
        Date checkOutDate = storeVO.getCheckOutDate();

        roomStoreVO.setHotelId(hotelId);
        roomStoreVO.setRoomId(roomId);
        roomStoreVO.setCheckInDate(checkInDate);
        roomStoreVO.setCheckOutDate(checkOutDate);
        roomStoreVO.setCount(1);

        //获取酒店名称
        ItripHotel hotel = itripHotelService.getItripHotelById(hotelId);
        roomStoreVO.setHotelName(hotel.getHotelName());
        //获取酒店价格
        ItripHotelRoom hotelroom = itripHotelRoomService.getItripHotelRoomById(roomId);

        roomStoreVO.setPrice(BigDecimal.valueOf(hotelroom.getRoomPrice()));
        //获取库存
        Map<String,Object> param = new HashMap<>();
        param.put("hotelId",hotelId);
        param.put("roomId",roomId);
        param.put("startTime",checkInDate);
        param.put("endTime",checkOutDate);
        List<ItripHotelTempStore> storeList =itripHotelTempStoreService.getItripStoreListByMap(param);
        //列表中根据store升序排序，第一个为最小值
        roomStoreVO.setStore(storeList.get(0).getStore());
        return roomStoreVO;
    }

}
