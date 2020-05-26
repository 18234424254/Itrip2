package cn.itrip.search.controller;

import cn.itrip.beans.vo.hotel.SearchHotCityVO;
import cn.itrip.search.service.ItripSearchService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.vo.ItripHotelVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.Page;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api
@RestController
@RequestMapping("/api")
public class ItripSearchController {
    @Resource
    private ItripSearchService itripSearchService;

    @PostMapping("/hotellist/searchItripHotelPage")
    public Dto searchHotelList(@RequestBody SearchHotelVO searchHotelVO) throws Exception {

        //验证目的地
         if (searchHotelVO==null||searchHotelVO.getDestination()==null){
            return DtoUtil.returnFail("目的地不能为空","20001");

        }
        Page<ItripHotelVO> page = itripSearchService.getHotelListByPage(searchHotelVO);
        //调用service分页查询
        //返回数据
        return DtoUtil.returnDataSuccess(page);
    }


    @RequestMapping(value = "/searchItripHotelListByHotCity", method = RequestMethod.POST)
    public Dto searchItripHotelListByHotCity(@RequestBody SearchHotCityVO searchHotCityVO) throws Exception {
        if (searchHotCityVO == null || searchHotCityVO.getCityId() == null) {
            return DtoUtil.returnFail("城市id不能为空", "20002");
        }
        List<ItripHotelVO> list =
                itripSearchService.getItripHotelListByCity(searchHotCityVO.getCityId(), searchHotCityVO.getCount());
        return DtoUtil.returnDataSuccess(list);

    }

}
