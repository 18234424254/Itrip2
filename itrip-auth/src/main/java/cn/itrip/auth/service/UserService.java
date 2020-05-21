package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;

public interface UserService {
    ItripUser getItripUserByUserCode(String userCode)throws Exception;

    void itriptxCreateuserItripUser(ItripUser itripUser)throws Exception;

    Boolean itriptxValidateSmsCode(String userCode, String smsCode) throws Exception;

    void updateItpirUser(ItripUser itripUser)throws  Exception;
}
