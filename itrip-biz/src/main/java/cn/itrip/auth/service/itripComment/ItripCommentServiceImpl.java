package cn.itrip.auth.service.itripComment;
import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.comment.ItripAddCommentVO;
import cn.itrip.beans.vo.comment.ItripListCommentVO;
import cn.itrip.mapper.itripComment.ItripCommentMapper;
import cn.itrip.beans.pojo.ItripComment;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.mapper.itripHotelOrder.ItripHotelOrderMapper;
import cn.itrip.mapper.itripImage.ItripImageMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import cn.itrip.common.Constants;
@Service
public class ItripCommentServiceImpl implements ItripCommentService {

    @Resource
    private ItripCommentMapper itripCommentMapper;
    @Resource
    private ItripHotelOrderMapper itripHotelOrderMapper;
    @Resource
    private ItripImageMapper itripImageMapper;

    public ItripComment getItripCommentById(Long id)throws Exception{
        return itripCommentMapper.getItripCommentById(id);
    }

    public List<ItripComment>	getItripCommentListByMap(Map<String,Object> param)throws Exception{
        return itripCommentMapper.getItripCommentListByMap(param);
    }

    public Integer getItripCommentCountByMap(Map<String,Object> param)throws Exception{
        return itripCommentMapper.getItripCommentCountByMap(param);
    }

    public Integer itriptxAddItripComment(ItripComment itripComment)throws Exception{
            itripComment.setCreationDate(new Date());
            return itripCommentMapper.insertItripComment(itripComment);
    }

    public Integer itriptxModifyItripComment(ItripComment itripComment)throws Exception{
        itripComment.setModifyDate(new Date());
        return itripCommentMapper.updateItripComment(itripComment);
    }

    public Integer itriptxDeleteItripCommentById(Long id)throws Exception{
        return itripCommentMapper.deleteItripCommentById(id);
    }

    public Page<ItripListCommentVO> queryItripCommentPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception{
        Integer total = itripCommentMapper.getItripCommentCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripComment> itripCommentList = itripCommentMapper.getItripCommentListByMap(param);
        page.setRows(itripCommentList);
        return page;
    }

    public Page<ItripListCommentVO> queryItripCommentVOPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception{
        Integer total = itripCommentMapper.getItripCommentCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripListCommentVO> itripCommentList = itripCommentMapper.getItripCommentVOListByMap(param);
        page.setRows(itripCommentList);
        return page;
    }

    @Override
    public void itriptxAddComment(ItripAddCommentVO commentVO,Long userid) throws Exception {
        //添加评论表记录
        ItripComment itripComment = new ItripComment();
        BeanUtils.copyProperties(commentVO,itripComment);
        itripComment.setCreationDate(new Date());
        itripComment.setUserId(userid);
        itripComment.setCreatedBy(userid);

        long tripMode = Long.parseLong(commentVO.getTripMode());
        itripComment.setTripMode(tripMode);
        itripComment.setScore((commentVO.getFacilitiesScore()+commentVO.getServiceScore()+commentVO.getHygieneScore()+commentVO.getPositionScore())/4);
        itripCommentMapper.insertItripComment(itripComment);//需返回评论id
        //添加图片表记录
        ItripImage[] itripImages = commentVO.getItripImages();
        for (ItripImage image : itripImages) {
            image.setType("2");
            image.setTargetId(itripComment.getId());//评论id
            image.setPosition(image.getPosition());
            image.setCreationDate(new Date());
            image.setCreatedBy(userid);
            itripImageMapper.insertItripImage(image);

        }
        //修改订单状态
        ItripHotelOrder order = new ItripHotelOrder();
        order.setId(commentVO.getOrderId());
        order.setOrderStatus(4);//已评论
        order.setModifyDate(new Date());
        itripHotelOrderMapper.updateItripHotelOrder(order);

    }

}
