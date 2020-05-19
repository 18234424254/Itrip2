package cn.itrip.auth.service;

import cn.itrip.auth.AuthExcepiton;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.common.*;
import com.alibaba.fastjson.JSON;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
@Service
public class TokenServiceImpl implements TokenService {
    @Resource
    private RedisAPI redisAPI;

    /**
     *
     * @param agent
     * @param itripUser
     * @return
     * @throws Exception
     */
    @Override
    public String generateToken(String agent, ItripUser itripUser) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(Constants.TOKEN_PRIFIX);
        //boolean b = UserAgentUtil.CheckAgent(agent);
        DeviceType deviceType = UserAgent.parseUserAgentString(agent).getOperatingSystem().getDeviceType();
        if (deviceType.getName().equals(DeviceType.MOBILE)) {
            sb.append("MOBILE");
        }else {
            sb.append("PC");
        }
        sb.append("-");
        sb.append(MD5.getMd5(itripUser.getUserCode(),32));
        sb.append("-");
        sb.append(itripUser.getId());
        sb.append("-");
        sb.append(LocalDateTime.now(ZoneOffset.of("+8")).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        sb.append("-");
        sb.append(MD5.getMd5(agent,6));
        return sb.toString();
    }

    /**
     * 缓存token
     * @param token
     * @throws Exception
     */
    @Override
    public void saveToken(String token,ItripUser itripUser) throws Exception {
        String userJson = JSON.toJSONString(itripUser);
        //获取itripUser
       if (token.startsWith(Constants.TOKEN_PRIFIX +"MOBILE")){
           redisAPI.set(token,userJson);
       }else {
           redisAPI.set(token,Constants.TOKEN_EXPIRE*60*60,userJson);//两个小时
       }


    }

    //处理token 生成-缓存 返回
    @Override
    public ItripTokenVO processToken(String agent, ItripUser user)throws Exception {
        //生成token
        String token = this.generateToken(agent, user);
        //缓存token
        this.saveToken(token,user);
        //返回token数据
        long expTime= Constants.TOKEN_EXPIRE*60*60*1000;
        long genTime = System.currentTimeMillis();
        ItripTokenVO tokenVO = new ItripTokenVO(token,expTime,genTime);
        return tokenVO;
    }



    /**
     * 验证token有效性
     * @param token
     * @param agent
     * @return
     */
    @Override
    public Boolean validateToken(String token, String agent)throws Exception {
        if (EmptyUtils.isEmpty(token)){
            //token不能为空 未携带token信息
            throw new AuthExcepiton("未携带token信息");
        }
        //判断是否是同一个客户端
        String[] tokenArr = token.split("-");

        if (!tokenArr[4].equals(MD5.getMd5(agent,6))) {
            //不是原来客户端了
            throw new AuthExcepiton("不是同一个客户端，未登录");
        }
        String genTimestr = tokenArr[3];
        LocalDateTime genTime = LocalDateTime.parse(genTimestr, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long time = genTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        if (System.currentTimeMillis()-time>Constants.TOKEN_EXPIRE*60*60*1000){
            //token过期失效 未登录状态
            return  false;
        }

            return true;
    }
    /**
     * 删除token
     * @param token
     * @throws Exception
     */
    @Override
    public void delToken(String token) throws Exception {
        redisAPI.delete(token);

    }

    /**
     * 置换token
     * @param token
     * @return
     * @throws Exception
     */
    @Override
    public String reloadToken(String token,String agent) throws Exception {
        String userJson = redisAPI.get(token);

        ItripUser itripUser = JSON.parseObject(userJson, ItripUser.class);
        

        String s = token.split("-")[3];
        LocalDateTime time = LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long genTime = time.toInstant(ZoneOffset.of("+8")).toEpochMilli();


        //判断是否在保护期(1小时保护期
        if(System.currentTimeMillis()-genTime<Constants.TOKEN_PROTECT_TIME*60*60*1000){
            throw new AuthExcepiton("保护期不允许更换");
        }
        //生成新token

        String newToken = this.generateToken(agent, itripUser);
        this.saveToken(newToken,itripUser);
        //修改旧token有效期2分钟后失效
        redisAPI.set(token,2*60,userJson);
        return newToken;
    }


}




































