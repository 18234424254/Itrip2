package cn.itrip.auth.service.itripHotelRoom;
import cn.itrip.beans.vo.hotelroom.ItripHotelRoomVO;
import cn.itrip.beans.vo.hotelroom.SearchHotelRoomVO;
import cn.itrip.common.DateUtil;
import cn.itrip.mapper.itripHotelRoom.ItripHotelRoomMapper;
import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import cn.itrip.common.Constants;
@Service
public class ItripHotelRoomServiceImpl implements ItripHotelRoomService {

    @Resource
    private ItripHotelRoomMapper itripHotelRoomMapper;

    public ItripHotelRoom getItripHotelRoomById(Long id)throws Exception{
        return itripHotelRoomMapper.getItripHotelRoomById(id);
    }

    public List<ItripHotelRoom>	getItripHotelRoomListByMap(Map<String,Object> param)throws Exception{
        return itripHotelRoomMapper.getItripHotelRoomListByMap(param);
    }

    public Integer getItripHotelRoomCountByMap(Map<String,Object> param)throws Exception{
        return itripHotelRoomMapper.getItripHotelRoomCountByMap(param);
    }

    public Integer itriptxAddItripHotelRoom(ItripHotelRoom itripHotelRoom)throws Exception{
            itripHotelRoom.setCreationDate(new Date());
            return itripHotelRoomMapper.insertItripHotelRoom(itripHotelRoom);
    }

    public Integer itriptxModifyItripHotelRoom(ItripHotelRoom itripHotelRoom)throws Exception{
        itripHotelRoom.setModifyDate(new Date());
        return itripHotelRoomMapper.updateItripHotelRoom(itripHotelRoom);
    }

    public Integer itriptxDeleteItripHotelRoomById(Long id)throws Exception{
        return itripHotelRoomMapper.deleteItripHotelRoomById(id);
    }

    public Page<ItripHotelRoom> queryItripHotelRoomPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception{
        Integer total = itripHotelRoomMapper.getItripHotelRoomCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripHotelRoom> itripHotelRoomList = itripHotelRoomMapper.getItripHotelRoomListByMap(param);
        page.setRows(itripHotelRoomList);
        return page;
    }

    /**
     * 查询酒店房型列表
     * @param searchHotelRoomVO 查询条件
     * @return
     * @throws Exception
     */
    @Override
    public List<ItripHotelRoomVO> getItripHotelRoomBySearchVo(SearchHotelRoomVO searchHotelRoomVO)throws Exception {
        Date startDate = searchHotelRoomVO.getStartDate();
        Date endDate = searchHotelRoomVO.getEndDate();
        //把时间区间内的日期转换为list集合
        List<Date> dateList = DateUtil.getBetweenDates(startDate, endDate);

        Map<String, Object> param = new HashMap<>();
        param.put("hotelId",searchHotelRoomVO.getHotelId());
        param.put("payType",searchHotelRoomVO.getPayType());
        param.put("isCancel",searchHotelRoomVO.getIsCancel());
        param.put("roomBedTypeId",searchHotelRoomVO.getRoomBedTypeId());
        param.put("isTimelyResponse",searchHotelRoomVO.getIsTimelyResponse());
        param.put("isHavingBreakfast",searchHotelRoomVO.getIsHavingBreakfast());
        param.put("isBook",searchHotelRoomVO.getIsBook());

        param.put("dateList",dateList);//自定义字段查询时间区间内是否有库存的房型



        List<ItripHotelRoom> roomList = itripHotelRoomMapper.getItripHotelRoomListByMap(param);
        List<ItripHotelRoomVO> roomVOList= new ArrayList<>();
        for (ItripHotelRoom room : roomList) {
            ItripHotelRoomVO roomVO = new ItripHotelRoomVO();
            BeanUtils.copyProperties(room,roomVO);
            //ItripHotelRoom中的房间价格weidouble无法自动cope到ItripHotelRoomVO里面 需要手动解决
            roomVO.setRoomPrice(new BigDecimal(room.getRoomPrice()));
            roomVOList.add(roomVO);
        }


        return roomVOList;
    }

}
