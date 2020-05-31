package cn.itrip.auth.controller;

import cn.itrip.auth.service.itripHotelRoom.ItripHotelRoomService;
import cn.itrip.auth.service.itripImage.ItripImageService;
import cn.itrip.auth.service.itripLabelDic.ItripLabelDicService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.ItripAreaDicVO;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.beans.vo.hotelroom.ItripHotelRoomVO;
import cn.itrip.beans.vo.hotelroom.SearchHotelRoomVO;
import cn.itrip.common.DtoUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/api/hotelroom")
public class HotelRoomController {
    @Resource
    private ItripImageService itripImageService;
    @Resource
    private ItripLabelDicService itripLabelDicService;
    @Resource
    private ItripHotelRoomService itripHotelRoomService;
    /**
     * 房型id
     * @param targetId
     * @return
     * @throws Exception
     */
    @GetMapping("/getimg/{targetId}")
    public Dto getImg(@PathVariable("targetId")Long targetId ) throws Exception {
        if (targetId == null) {
            return DtoUtil.returnFail("房型id不能为空","100302");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("type","1");
        param.put("targetId",targetId);
        List<ItripImage> imageList = itripImageService.getItripImageListByMap(param);
        List<ItripImageVO> imageVOS = new ArrayList<>();
        for (ItripImage image : imageList) {
            ItripImageVO itripImageVO = new ItripImageVO();
            BeanUtils.copyProperties(image,itripImageVO);
            imageVOS.add(itripImageVO);
        }
        return DtoUtil.returnDataSuccess(imageVOS);
    }

    /**
     * 查询酒店床型列表
     * @return
     * @throws Exception
     */
    @GetMapping("/queryhotelroombed")
    public Dto<List<ItripLabelDicVO>> queryRoomBedList() throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("parentId",1);
        List<ItripLabelDic> labelDicList = itripLabelDicService.getItripLabelDicListByMap(param);
        List<ItripLabelDicVO> itripLabelDicVOS= new ArrayList<>();
        for (ItripLabelDic labelDic : labelDicList) {

            ItripLabelDicVO dicVO = new ItripLabelDicVO();
            BeanUtils.copyProperties(labelDic,dicVO);
            itripLabelDicVOS.add(dicVO);
        }
        return DtoUtil.returnDataSuccess(itripLabelDicVOS);
    }

    /**
     * 根据搜索条件查询房型列表
     * @param roomVO
     * @return
     */
    @PostMapping("/queryhotelroombyhotel")
    public Dto queryHotelRoomByHotel(@RequestBody SearchHotelRoomVO roomVO) throws Exception {
        //验证酒店id不能为空
        if (roomVO == null||roomVO.getHotelId()==null) {
            return DtoUtil.returnFail("酒店id不能为空","100303");
        }
        //验证入离时间
        Date startDate = roomVO.getStartDate();
        Date endDate = roomVO.getEndDate();
        if (startDate ==null|| endDate ==null){
            return DtoUtil.returnFail("入住和退房时间不能为空","100304");
        }
        //入住时间不能晚于退房时间
        if (startDate.getTime()>endDate.getTime()){
            return  DtoUtil.returnFail("入住时间不能晚于退房时间","100305");
        }
        //调用业务逻辑查询房型列表
        List<ItripHotelRoomVO> roomVOList= itripHotelRoomService.getItripHotelRoomBySearchVo(roomVO);

        return DtoUtil.returnDataSuccess(roomVOList);

    }















}
