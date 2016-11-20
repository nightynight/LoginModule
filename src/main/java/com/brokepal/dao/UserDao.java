package com.brokepal.dao;

import com.brokepal.entity.User;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2016/11/3.
 */
public interface UserDao {
    int createUser(User user);
    int updateUser(User user);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    int updateStatus(@Param("status") int status, @Param("email") String email);
    int updateActivateCodeAndTime(@Param("activateCode") String activateCode, @Param("sendActivateCodeTime") Timestamp sendActivateCodeTime, @Param("email") String email);
    int updateValidateCodeAndTime(@Param("validateCode") String validateCode, @Param("sendValidateCodeTime") Timestamp sendValidateCodeTime, @Param("email") String email);
}
