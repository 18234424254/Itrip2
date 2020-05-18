package cn.itrip.auth.controller;

import cn.itrip.auth.service.UserService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.userinfo.ItripUserVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.ErrorCode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class UserController {
    @Resource
    private UserService userService;

    @RequestMapping(value = "/registerbyphone",method = RequestMethod.POST)
    public Dto registByPhone(@RequestBody ItripUserVO userVO) throws Exception {
        String userCode=userVO.getUserCode();
        //验证手机号是否合法
        if (!valiPhone(userCode)){
            return DtoUtil.returnFail("请输入正确的手机号", ErrorCode.AUTH_ILLEGAL_USERCODE);
        }

        //验证是否已经注册过
        ItripUser user = userService.getItripUserByUserCode(userCode);

        if (EmptyUtils.isNotEmpty(user)) {
            return DtoUtil.returnFail("已注册过",ErrorCode.AUTH_USER_ALREADY_EXISTS);
        }
        //注册用户（写入数据库 未激活
        ItripUser itripUser = new ItripUser();
        BeanUtils.copyProperties(userVO,itripUser);

        userService.itriptxCreateuserItripUser(itripUser);

        //写入数据库
        //返回结果
        return DtoUtil.returnSuccess("注册成功");
    }
    //正则验证手机号是否合法
    private boolean valiPhone(String phone) {
        String  regex="^1[3578]{1}\\d{9}$";
        return Pattern.compile(regex).matcher(phone).find();
    }

    @RequestMapping(value = "/validatephone",method = RequestMethod.PUT)
    public Dto validatephone(@RequestParam("user") String userCode,@RequestParam("code") String smsCode) throws Exception {
        Boolean b=  userService.itriptxValidateSmsCode(userCode,smsCode);
        if (b){
            return DtoUtil.returnSuccess("验证成功");
        }else {
            return DtoUtil.returnFail("短信验证码验证失败",ErrorCode.AUTH_ACTIVATE_FAILED);
        }

    }


}
