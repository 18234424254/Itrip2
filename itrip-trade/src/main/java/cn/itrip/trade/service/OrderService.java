package cn.itrip.trade.service;

import cn.itrip.beans.pojo.ItripHotelOrder;

public interface OrderService {

    void processupdateOrderStatus(ItripHotelOrder order)throws Exception;
}
