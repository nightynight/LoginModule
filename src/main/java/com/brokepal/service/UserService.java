package com.brokepal.service;

import com.brokepal.dao.UserDao;
import com.brokepal.entity.User;
import com.brokepal.exception.ValidateException;
import com.brokepal.utils.CommonUtil;
import com.brokepal.utils.MD5Util;
import com.brokepal.utils.SendEmail;
import com.brokepal.utils.XmlUtil;
import com.sun.mail.smtp.SMTPAddressFailedException;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

/**
 * Created by Administrator on 2016/11/3.
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    /**
     * 创建用户
     * @param user
     * @return int 成功则返回1，失败则返回0
     * @throws IllegalArgumentException 参数user不能为null，user的nickname、username,password,email都不能为null
     */
    public int createUser(User user) {
        if (user == null)
            throw new IllegalArgumentException("user can't be null");
        if (user.getNickname() == null || user.getUsername() == null || user.getPassword() == null || user.getEmail() == null || user.getSalt() == null)
            throw new IllegalArgumentException("nickname,username,password,email,salt and validateCode can't be null");
        if (user.getPhone() == null)
            user.setPhone("");
        try {
            String activateCode = MD5Util.string2MD5("email="  + user.getEmail());
            user.setActivateCode(activateCode);
            int count = userDao.createUser(user);
            if (count >= 1){
                System.out.println("success");
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 修改用户信息
     * @param user
     * @return int 成功则返回1，失败则返回0
     * @throws IllegalArgumentException 参数user不能为null，user的nickname、username,password,email都不能为null
     */
    public int updateUser(User user) {
        if (user == null)
            throw new IllegalArgumentException("user can't be null");
        if (user.getNickname() == null || user.getUsername() == null || user.getPassword() == null || user.getEmail() == null)
            throw new IllegalArgumentException("nickname,username,password,email and validateCode can't be null");
        if (user.getPhone() == null)
            user.setPhone("");
        try {
            String validateCode = MD5Util.string2MD5("email="  + user.getEmail());
            user.setValidateCode(validateCode);
            int count = userDao.updateUser(user);
            if (count >= 1){
                System.out.println("success");
                return 1;
            }
            throw new Exception("there's no data whose username is '" + user.getUsername() +"'");
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 根据用户名查询用户
     * @param username
     * @return User 如果查询到结果，则返回，否则，返回null
     * @throws IllegalArgumentException username不能为null或空字符串
     */
    public User getUserByUsername(String username) {
        if (username == null || "".equals(username))
            throw new IllegalArgumentException("username can't be null or empty");
        try {
            return userDao.getUserByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据邮箱查询用户
     * @param emial
     * @return User 如果查询到结果，则返回，否则，返回null
     * @throws IllegalArgumentException username不能为null或空字符串
     */
    public User getUserByEmail(String emial) {
        if (emial == null || "".equals(emial))
            throw new IllegalArgumentException("emial can't be null or empty");
        try {
            return userDao.getUserByEmail(emial);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送激活邮件
     * @param email
     * @throws SMTPAddressFailedException 当输入的邮箱无效时抛出该异常
     */
    public int sendActivateEmail(String email) throws SMTPAddressFailedException {
        ///邮件的内容
        StringBuffer sb=new StringBuffer("点击下面链接激活账号，24小时生效，否则请重新发送激活邮件，链接只能使用一次，请尽快激活！\n");
        sb.append("http://localhost:8081/static/activate?email=");
        sb.append(email);
        sb.append("&activateCode=");
        String activateCode = MD5Util.string2MD5(email + new Date().getTime());
        sb.append(activateCode);
        //发送邮件
        SendEmail.send(email, sb.toString(),"账号激活邮件");
        return userDao.updateActivateCodeAndTime(activateCode,new Timestamp(new Date().getTime()),email);//更新发送激活邮件时间
    }

    /**
     * 激活
     * @param email
     * @param activateCode
     * @param language 显示语言（中文、英文等）
     * @throws ValidateException
     * @throws IllegalArgumentException email、validateCode不能为null
     * @throws DocumentException 由于是根据language来读取相应的配置文件，所以可能会找不到文件，该API的用户需要做做异常处理
     */
    public int activate(String email , String activateCode, String language) throws ValidateException, DocumentException {
        if (email == null || activateCode == null)
            throw new IllegalArgumentException("email or validateCode can not be null.");
        //数据访问层，通过email获取用户信息
        User user=userDao.getUserByEmail(email);
        //验证用户是否存在
        if(user!=null) {
            //验证用户激活状态
            if(user.getStatus()==0) {//没激活
                Date currentTime = new Date();//获取当前时间
                //验证链接是否过期
                if(currentTime.getTime() - user.getSendActivateCodeTime().getTime() < 24 * 60 * 60 * 1000) {
                    if(activateCode.equals(user.getActivateCode())) {//验证激活码是否正确
                         return userDao.updateStatus(1,email);//把状态改为已激活
                    } else {
                        throw new ValidateException(XmlUtil.getNodeText(language,"login.pleaseActivate"));
                    }
                } else {
                    throw new ValidateException(XmlUtil.getNodeText(language,"login.activateCodeExpire"));
                }
            } else {
                throw new ValidateException(XmlUtil.getNodeText(language,"login.activated"));
            }
        } else {
            throw new ValidateException(XmlUtil.getNodeText(language,"login.emailNotAvailable"));
        }
    }

    /**
     * 发送重置密码的验证码
     * @param email
     * * @throws SMTPAddressFailedException 当输入的邮箱无效时抛出该异常
     */
    public int sendValidateCodeEmial(String email) throws SMTPAddressFailedException{
        String codes = "0123456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ";
        String validateCode = CommonUtil.createRandomString(6);
        ///邮件的内容
        String content = "请在15分钟内输入以下验证码进行重置密码： " + validateCode;
        //发送邮件
        SendEmail.send(email, content, "重置密码邮件");
        return userDao.updateValidateCodeAndTime(validateCode,new Timestamp(new Date().getTime()),email);//更新发送激活邮件时间
    }
}
