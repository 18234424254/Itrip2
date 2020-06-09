package cn.itrip.auth.controller;

import cn.itrip.auth.service.itripComment.ItripCommentService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.comment.ItripAddCommentVO;
import cn.itrip.beans.vo.comment.ItripListCommentVO;
import cn.itrip.beans.vo.comment.ItripSearchCommentVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.Page;
import cn.itrip.common.SystemConfig;
import cn.itrip.common.ValidationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Resource
    private ItripCommentService itripCommentService;
    @Resource
    private ValidationToken validationToken;
    @Resource
    private SystemConfig systemConfig;


    @PostMapping("/getcommentlist")
    public Dto getCommentList(@RequestBody ItripSearchCommentVO commentVO) throws Exception {
        Long hotelId = commentVO.getHotelId();
        Integer isHavingImg = commentVO.getIsHavingImg();
        Integer isOk = commentVO.getIsOk();
        Integer pageNo = commentVO.getPageNo();
        Integer pageSize = commentVO.getPageSize();
        if (hotelId == null && isOk == null && isHavingImg == null && pageNo == null && pageSize == null) {
            return DtoUtil.returnFail("参数不能为空", "100020 ");
        }
        //查询所有有图和无图的评论
        if (isHavingImg == -1) {
            isHavingImg = null;
        }
        //查询所有值得推荐和有待改善的评论
        if (isOk == -1) {
            isOk = null;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("hotelId", hotelId);
        param.put("isHavingImg", isHavingImg);
        param.put("isOk", isOk);
        Page<ItripListCommentVO> page = itripCommentService.queryItripCommentVOPageByMap(param, pageNo, pageSize);
        return DtoUtil.returnDataSuccess(page);

    }

    @PostMapping("/upload")
    public Dto upload(MultipartFile[] uploadfile, HttpServletRequest request) throws IOException {
        //登录验证
        String token = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(token);
        if (currentUser == null) {
            return DtoUtil.returnFail("未登录状态", "1000000");
        }
        //返回的图片访问路径
        List<String> pathUrl = new ArrayList<>();

        for (MultipartFile multipartFile : uploadfile) {
            String filename = multipartFile.getOriginalFilename();
            System.out.println("文件名=" + filename);
            //新文件名
            String newFileName = currentUser.getId() + "-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 100000);
            multipartFile.transferTo(new File("D:/songjian/", newFileName));
            pathUrl.add(systemConfig.getVisitImgUrlString() + newFileName);
        }

        return DtoUtil.returnDataSuccess(pathUrl);
    }

    @PostMapping("/add")
    public Dto addComment(@RequestBody ItripAddCommentVO commentVO, HttpServletRequest request) throws Exception {
        //登录验证
//        String token = request.getHeader("token");
//        ItripUser currentUser = validationToken.getCurrentUser(token);
//        if (currentUser == null) {
//            return DtoUtil.returnFail("未登录状态", "100000");
//        }
        if (commentVO == null && commentVO.getOrderId() == null) {
            return DtoUtil.returnFail("必填参数不能为空", "100021");
        }
        //添加评论
      //  itripCommentService.itriptxAddComment(commentVO, currentUser.getId());
        itripCommentService.itriptxAddComment(commentVO, 29l);
        return DtoUtil.returnDataSuccess("添加评论成功");
    }


}
