package cn.itrip.auth.controller;

import cn.itrip.auth.service.itripHotelOrder.ItripHotelOrderService;
import cn.itrip.auth.service.itripHotelTempStore.ItripHotelTempStoreService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.order.RoomStoreVO;
import cn.itrip.beans.vo.order.ValidateRoomStoreVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ValidationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/hotelorder")
public class HotelOrderController {
    @Resource
    private ValidationToken validationToken;
    @Resource
    private ItripHotelOrderService itripHotelOrderService;
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
}
