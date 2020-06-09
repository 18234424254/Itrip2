package cn.itrip.auth.service.itripComment;
import cn.itrip.beans.pojo.ItripComment;
import java.util.List;
import java.util.Map;

import cn.itrip.beans.vo.comment.ItripAddCommentVO;
import cn.itrip.beans.vo.comment.ItripListCommentVO;
import cn.itrip.common.Page;
/**
* Created by shang-pc on 2015/11/7.
*/
public interface ItripCommentService {

    public ItripComment getItripCommentById(Long id)throws Exception;

    public List<ItripComment>	getItripCommentListByMap(Map<String,Object> param)throws Exception;

    public Integer getItripCommentCountByMap(Map<String,Object> param)throws Exception;

    public Integer itriptxAddItripComment(ItripComment itripComment)throws Exception;

    public Integer itriptxModifyItripComment(ItripComment itripComment)throws Exception;

    public Integer itriptxDeleteItripCommentById(Long id)throws Exception;

    public Page<ItripListCommentVO> queryItripCommentPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception;

    public Page<ItripListCommentVO> queryItripCommentVOPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception;
    //添加评论图片
    void itriptxAddComment(ItripAddCommentVO commentVO, Long userid)throws Exception;
}
