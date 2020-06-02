package cn.itrip.auth.service.itripHotelOrder;
import cn.itrip.auth.service.itripHotel.ItripHotelService;
import cn.itrip.auth.service.itripHotelRoom.ItripHotelRoomService;
import cn.itrip.auth.service.itripHotelTempStore.ItripHotelTempStoreService;
import cn.itrip.beans.pojo.*;
import cn.itrip.beans.vo.order.RoomStoreVO;
import cn.itrip.beans.vo.order.ValidateRoomStoreVO;
import cn.itrip.common.BigDecimalUtil;
import cn.itrip.mapper.itripHotelOrder.ItripHotelOrderMapper;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.mapper.itripOrderLinkUser.ItripOrderLinkUserMapper;
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

    @Resource
    private ItripOrderLinkUserMapper itripOrderLinkUserMapper;


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
        ItripHotel hotel = itripHotelService.getItripHotelById(hotelId);
        ItripHotelRoom hotelroom = itripHotelRoomService.getItripHotelRoomById(roomId);

        roomStoreVO.setHotelId(hotelId);
        roomStoreVO.setRoomId(roomId);
        roomStoreVO.setCheckInDate(checkInDate);
        roomStoreVO.setCheckOutDate(checkOutDate);
        roomStoreVO.setCount(1);
        roomStoreVO.setHotelName(hotel.getHotelName());//获取酒店名称
        roomStoreVO.setPrice(BigDecimal.valueOf(hotelroom.getRoomPrice()));//获取酒店价格

        Map<String,Object> param = new HashMap<>();//获取库存

        param.put("hotelId",hotelId);
        param.put("roomId",roomId);
        param.put("startTime",checkInDate);
        param.put("endTime",checkOutDate);
        List<ItripHotelTempStore> storeList =itripHotelTempStoreService.getItripStoreListByMap(param);
        //列表中根据store升序排序，第一个为最小值
        roomStoreVO.setStore(storeList.get(0).getStore());
        return roomStoreVO;
    }

    /**
     * 验证是否有库存
     * @param storeVO
     * @return
     * @throws Exception
     */
    @Override
    public boolean validateRoomStore(ValidateRoomStoreVO storeVO) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("hotelId",storeVO.getHotelId());
        param.put("roomId",storeVO.getRoomId());
        param.put("startTime",storeVO.getCheckInDate());
        param.put("endTime",storeVO.getCheckOutDate());
        //获取库存列表
        List<ItripHotelTempStore> storeList =itripHotelTempStoreService.getItripStoreListByMap(param);
        //判断是否有库存（用最小值判断
        if (storeList.get(0).getStore()-storeVO.getCount()>=0){
            return true;
        }

        return false;
    }

    /**
     * 计算订单金额
     * @param roomPrice
     * @param bookingDays
     * @param count
     * @return
     * @throws Exception
     */
    @Override
    public Double calcPayAmount(Double roomPrice, int bookingDays, Integer count) throws Exception {
        BigDecimal bigDecimal = BigDecimalUtil.OperationASMD(roomPrice, bookingDays * count, BigDecimalUtil.BigDecimalOprations.multiply, 2, BigDecimal.ROUND_FLOOR);
        return bigDecimal.doubleValue();
    }

    /**
     * 生成订单
     * @param order
     * @param linkUser
     */
    @Override
    public Long itriptxAddItripHotelOrder(ItripHotelOrder order, List<ItripUserLinkUser> linkUser) throws Exception {
        Long orderId = order.getId();
        if (orderId == null) {//订单未生成过，新生成订单
            //数据库插入订单记录
            itripHotelOrderMapper.insertItripHotelOrder(order);
            orderId=order.getId();
            //数据库插入订单联系人记录

        }else {//已有订单id
            itripHotelOrderMapper.updateItripHotelOrder(order);
            //删除联系人和入住人记录
            itripOrderLinkUserMapper.deleteItripOrderLinkUserByOrderId(orderId);

        }
        //重新添加入住人订单
        for (ItripUserLinkUser userLinkUser : linkUser) {
            ItripOrderLinkUser orderLinkUser = new ItripOrderLinkUser();
            orderLinkUser.setOrderId(order.getId());
            orderLinkUser.setLinkUserId(userLinkUser.getId());
            orderLinkUser.setLinkUserName(userLinkUser.getLinkUserName());
            orderLinkUser.setCreationDate(new Date());
            itripOrderLinkUserMapper.insertItripOrderLinkUser(orderLinkUser);
        }
        return order.getId();
    }

}
