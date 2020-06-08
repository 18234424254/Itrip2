package cn.itrip.trade.service;

import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.pojo.ItripTradeEnds;
import cn.itrip.mapper.itripHotelOrder.ItripHotelOrderMapper;
import cn.itrip.mapper.itripTradeEnds.ItripTradeEndsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private ItripHotelOrderMapper itripHotelOrderMapper;

    @Resource
    private ItripTradeEndsMapper itripTradeEndsMapper;


    @Override
    public void processupdateOrderStatus(ItripHotelOrder order) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("oderNo",order.getOrderNo());
        //更新订单状态
        List<ItripHotelOrder> orderList = itripHotelOrderMapper.getItripHotelOrderListByMap(param);
        Long id = orderList.get(0).getId();
        order.setId(id);
        itripHotelOrderMapper.updateItripHotelOrder(order);
        //更新库存---（插入交易中间数据表
        ItripTradeEnds itripTradeEnds = new ItripTradeEnds();
        itripTradeEnds.setFlag(0);
        itripTradeEnds.setOrderNo(order.getOrderNo());
        itripTradeEnds.setId(id);
        itripTradeEndsMapper.insertItripTradeEnds(itripTradeEnds);




    }
}
