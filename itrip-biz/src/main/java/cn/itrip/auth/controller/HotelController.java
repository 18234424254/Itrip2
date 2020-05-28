package cn.itrip.auth.controller;

import cn.itrip.auth.service.itripAreaDic.ItripAreaDicService;
import cn.itrip.auth.service.itripHotel.ItripHotelService;
import cn.itrip.auth.service.itripImage.ItripImageService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.ItripAreaDicVO;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.common.DtoUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HotelController {
    @Resource
    private ItripAreaDicService itripAreaDicService;
    @Resource
    private ItripImageService itripImageService;
    @Resource
    private ItripHotelService itripHotelService;

    /**
     * 查询热门城市
     * @param type 是否在国内  1 国内 2 国外
     * @return
     * @throws Exception
     */
    @GetMapping("/hotel/queryhotcity/{type}")
    public Dto queryHotCity(@PathVariable("type") String type) throws Exception {
        if (type==null){
            return DtoUtil.returnFail("type不能为空","10201");
        }
        //查询热门城市
        Map<String,Object> param = new HashMap<>();
        param.put("isChina",type);
        param.put("isHot",1);
        List<ItripAreaDic> list = itripAreaDicService.getItripAreaDicListByMap(param);
        //封装结果
        List<ItripAreaDicVO> result = new ArrayList<>();
        for (ItripAreaDic areaDic : list) {
            ItripAreaDicVO dicVO = new ItripAreaDicVO();
            BeanUtils.copyProperties(areaDic,dicVO);
            result.add(dicVO);
        }
        return DtoUtil.returnDataSuccess(result);
    }

    /**
     * 查询城市商圈
     * @param cityId
     * @return
     * @throws Exception
     */
    @GetMapping("/hotel/querytradearea/{cityId}")
    public Dto queryTradeArea(@PathVariable("cityId") Long cityId) throws Exception {
        if (cityId == null) {
            return DtoUtil.returnFail("cityId不能为空","10203 ");
        }
        //调用service查询城市商圈

        Map<String, Object> param = new HashMap<>();
        param.put("isTradingArea",1);
        param.put("parent",cityId);

        List<ItripAreaDic> list = itripAreaDicService.getItripAreaDicListByMap(param);
        //封装结果
        List<ItripAreaDicVO> result = new ArrayList<>();
        for (ItripAreaDic areaDic : list) {
            ItripAreaDicVO dicVO = new ItripAreaDicVO();
            BeanUtils.copyProperties(areaDic,dicVO);
            result.add(dicVO);
        }
        return DtoUtil.returnDataSuccess(result);
    }

    /**
     * 更加酒店id查询酒店图片
     * @param targetId
     * @return
     */
    @GetMapping("/hotel/getimg/{targetId}")
    public Dto getHotelImg(@PathVariable("targetId") Long targetId) throws Exception {
        if (targetId == null) {
            return DtoUtil.returnFail("目标id不能为空","100213");
        }
        //调用service查询酒店图片
        Map<String, Object> param = new HashMap<>();
        param.put("targetId",targetId);
        param.put("type","0");//酒店图片
        List<ItripImage> list = itripImageService.getItripImageListByMap(param);
        //封装结果
        List<ItripImageVO> result = new ArrayList<>();

        for (ItripImage image : list) {
            ItripImageVO imageVO = new ItripImageVO();
            BeanUtils.copyProperties(image,imageVO);
            result.add(imageVO);
        }

        return DtoUtil.returnDataSuccess(result);
    }


    @GetMapping("/hotel/getvideodesc/{hotelId}")
    public Dto getVideoDesc(@PathVariable("hotelId") Long hotelId) throws Exception {
        if (hotelId == null) {
            return DtoUtil.returnFail("酒店id不能为空","100215");
        }
        //调用service查询酒店视频信息
        HotelVideoDescVO videoDescVO = itripHotelService.getItripHotelVideoDesc(hotelId);
        //返回结果
        return  DtoUtil.returnDataSuccess(videoDescVO);
    }



}
