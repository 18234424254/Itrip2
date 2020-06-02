package cn.itrip.auth.controller;

import cn.itrip.auth.service.itripHotelOrder.ItripHotelOrderService;
import cn.itrip.auth.service.itripHotelRoom.ItripHotelRoomService;
import cn.itrip.auth.service.itripHotelTempStore.ItripHotelTempStoreService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.pojo.ItripUserLinkUser;
import cn.itrip.beans.vo.order.ItripAddHotelOrderVO;
import cn.itrip.beans.vo.order.RoomStoreVO;
import cn.itrip.beans.vo.order.ValidateRoomStoreVO;
import cn.itrip.common.*;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotelorder")
public class HotelOrderController {
    @Resource
    private ValidationToken validationToken;
    @Resource
    private ItripHotelOrderService itripHotelOrderService;
    @Resource
    private ItripHotelRoomService itripHotelRoomService;
    @Resource
    private SystemConfig systemConfig;


    /**
     * 获取订单预定信息（剩余库存
     * @param storeVO
     * @param request
     * @return
     */
    @PostMapping("/getpreorderinfo")
    public Dto getPreOrderInfo(@RequestBody ValidateRoomStoreVO storeVO, HttpServletRequest request) throws Exception {
        //登录验证
//        ItripUser user = validationToken.getCurrentUser(request.getHeader("token"));
//        if (user == null) {
//            return DtoUtil.returnFail("token认证失败,请重新登录","100000");
//        }
        //参数验证
        if (storeVO.getHotelId()==null||storeVO.getRoomId()==null){
            return DtoUtil.returnFail("酒店id和房间id不能为空","100510");
        }
        //查库存
        RoomStoreVO roomStoreVO =  itripHotelOrderService.getPreOrderInfo(storeVO);

        //返回数据
        return DtoUtil.returnDataSuccess(roomStoreVO);
    }

    @PostMapping("/validateroomstore")
    public Dto validateRoomStore(@RequestBody ValidateRoomStoreVO storeVO,HttpServletRequest request) throws Exception {
        //登录验证
        ItripUser user = validationToken.getCurrentUser(request.getHeader("token"));
        if (user == null) {
            return DtoUtil.returnFail("token认证失败,请重新登录","100000");
        }
        //参数验证
        if (storeVO.getHotelId()==null||storeVO.getRoomId()==null){
            return DtoUtil.returnFail("酒店id和房间id不能为空","100510");
        }
        if (storeVO.getCheckInDate()==null||storeVO.getCheckOutDate()==null){
            return DtoUtil.returnFail("入住和退房时间不能为空","100511");
        }
        //查询是否有库存
        boolean isHaving = itripHotelOrderService.validateRoomStore(storeVO);
        //封装返回结果
        Map<String,Object> result = new HashMap<>();
        result.put("success",isHaving);


        return DtoUtil.returnDataSuccess(result);
    }

    @PostMapping("/addhotelorder")
    public Dto addHotelOrderVO(@RequestBody ItripAddHotelOrderVO orderVO,HttpServletRequest request) throws Exception {
        //登录验证
        String token = request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(token);
        if (user == null) {
            return DtoUtil.returnFail("token认证失败,请重新登录","100000");
        }

        Long roomId = orderVO.getRoomId();
        if (roomId == null||orderVO.getHotelId()==null||orderVO.getCount()==null) {
            return DtoUtil.returnFail("必填数据不能为空,请填写订单","100506");
        }
        //生成订单
        ItripHotelOrder order = new ItripHotelOrder();
        BeanUtils.copyProperties(orderVO,order);

        order.setUserId(user.getId());
        order.setOrderStatus(0);
        if (token.startsWith(Constants.TOKEN_PRIFIX+"PC")) {
            order.setBookType(0);
        }else if (token.startsWith(Constants.TOKEN_PRIFIX+"MOBILE")){
            order.setBookType(1);
        }else{
            order.setBookType(2);
        }
        order.setCreationDate(new Date());
        order.setCreatedBy(user.getId());
        //订单天数
        int bookingDays = DateUtil.getBetweenDates(orderVO.getCheckInDate(), orderVO.getCheckOutDate()).size() - 1;
        order.setBookingDays(bookingDays);
        //支付金额(单价，天数，房间数
        ItripHotelRoom room = itripHotelRoomService.getItripHotelRoomById(roomId);
        Double roomPrice = room.getRoomPrice();

        //计算总金额
        Double payAmount = itripHotelOrderService.calcPayAmount(roomPrice,bookingDays,order.getCount());
        order.setPayAmount(payAmount);

        //联系人
        List<ItripUserLinkUser> linkUser = orderVO.getLinkUser();
        StringBuffer linkUserNames = new StringBuffer();
        for (ItripUserLinkUser userLinkUser : linkUser) {
            linkUserNames.append(userLinkUser.getLinkUserName()+",");
        }
        //去除最后一个逗号
        linkUserNames.substring(0,linkUserNames.length()-1);
        order.setLinkUserName(linkUserNames.toString());
        //订单编号（唯一字符串,机器码+时间戳+MD5
        StringBuffer orderNo=new StringBuffer();
        orderNo.append(systemConfig.getMachineCode());
        orderNo.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        orderNo.append(MD5.getMd5(""+roomId+System.currentTimeMillis()+(Math.random()*900000+100000),6));
        order.setOrderNo(orderNo.toString());


        Long orderId = itripHotelOrderService.itriptxAddItripHotelOrder(order, linkUser);
        Map<String,Object> result= new HashMap<>();
        result.put("orderId",orderId);

        return DtoUtil.returnDataSuccess(result);

    }


}
























