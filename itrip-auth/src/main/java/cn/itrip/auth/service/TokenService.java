package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;

public interface TokenService
{
    String generateToken(String agent, ItripUser itripUser)throws Exception;

    void saveToken(String token,ItripUser itripUser)throws Exception;

    Boolean validateToken(String token, String agent)throws Exception;

    ItripTokenVO processToken(String agent, ItripUser user)throws Exception;

    void delToken(String token)throws Exception;

    String reloadToken(String token,String agent)throws  Exception;
}
