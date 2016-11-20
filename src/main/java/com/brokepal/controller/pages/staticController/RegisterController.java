package com.brokepal.controller.pages.staticController;

import com.brokepal.dto.SendEmailResult;
import com.brokepal.entity.User;
import com.brokepal.exception.RequestParamException;
import com.brokepal.exception.ValidateException;
import com.brokepal.service.UserService;
import com.brokepal.utils.CommonUtil;
import com.brokepal.utils.MD5Util;
import com.brokepal.utils.RSACryptoUtil;
import com.brokepal.utils.XmlUtil;
import com.sun.mail.smtp.SMTPAddressFailedException;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;

/**
 * Created by Administrator on 2016/11/4.
 */
@Controller
@RequestMapping(value="/static")
public class RegisterController {
    private String str_privateKey = null;

    @Autowired
    private UserService userService;

    @RequestMapping(value="/register")
    public String Login(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session=req.getSession();
        session.removeAttribute("registerFailInfo");
        String nickname = req.getParameter("nickname");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String passwordConfirm = req.getParameter("passwordConfirm");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String registerTimestamp = req.getParameter("registerTimestamp");

        do {
            //1.判断是否是F5刷新引起的表单重复提交
            String oldTimestamp = session.getAttribute("registerTimestamp") == null ? "" : session.getAttribute("registerTimestamp").toString();	//上次提交请求的时间
            if (oldTimestamp.equals(registerTimestamp)) //对比两次提交时间，不一样则表示不是重复提交
                break;
            else
                session.setAttribute("registerTimestamp",registerTimestamp);

            //2.验证nickname
            if (nickname == null || "".equals(nickname))
                break;

            //3.验证username、email
            if (username == null || "".equals(username) || email == null || "".equals(email))
                break;

            //4.验证邮箱是否可用
            if (userService.getUserByEmail(email) != null) {
                session.setAttribute("registerFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"),"login.emailHasBeenUsed"));
                break;
            }

            //5.验证用户名是否可用
            if (userService.getUserByUsername(username) != null) {
                session.setAttribute("registerFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"),"login.usernameHasBeenUsed"));
                break;
            }

            //5.判断两次密码是否相同
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) RSACryptoUtil.getPrivateKey(str_privateKey);
            String srcPassword = RSACryptoUtil.RSADecodeWithPrivateKey(rsaPrivateKey,password);
            String srcPasswordConfirm = RSACryptoUtil.RSADecodeWithPrivateKey(rsaPrivateKey,passwordConfirm);
            if (!srcPassword.equals(srcPasswordConfirm)) { //两次输入密码是否相同
                session.setAttribute("registerFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"), "login.passwordNotSame"));
                break;
            }

            //6.验证密码的有效性
            if (srcPassword.length() < 0) {  //TODO 验证密码是否符合规范
                session.setAttribute("registerFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"), "login.passwordNotAvailable"));
                break;
            }

            //通过所有验证，创建用户
            String salt = CommonUtil.createRandomString(10);    //随机生成一个盐
            String passwordMD5 = MD5Util.string2MD5(MD5Util.string2MD5(srcPassword) + salt);
            if (userService.createUser(new User(nickname,username,passwordMD5,salt,email,phone)) > 0){ //如果创建成功
                userService.sendActivateEmail(email);
                session.setAttribute("email",email);
                session.removeAttribute("loginFailInfo");
                String fromPage = req.getParameter("from");
                if (fromPage == null || "".equals(fromPage))
                    resp.sendRedirect(req.getContextPath()+"/static/emailActivate");
                else
                    resp.sendRedirect(req.getContextPath() + "/static/emailActivate?from=" + fromPage);
                session.removeAttribute("registerFailInfo");
                return null;
            } else {
                session.setAttribute("registerFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"),"login.error"));
            }
        } while (false);

        RSACryptoUtil.KeyPairOfString keyPairOfString = RSACryptoUtil.makeBothKeyOfString();
        String str_publicKey = keyPairOfString.getPublicKey();	//publicKey会发往客户端，将用户的密码加密
        session.setAttribute("publicKey", str_publicKey.replace("\n",""));	//将公钥放入session，发往客户端，将用户的密码加密
        str_privateKey = keyPairOfString.getPrivateKey();		//privateKey用于将接受到的加密后的密码进行解密
        return "register";
    }

    /**
     * 验证用户名存不存在
     * @param req 请求中必须带“username”，否则抛出RequestParamException异常
     * @param resp
     * @return
     * @throws RequestParamException
     */
    @RequestMapping(value="/isUsernameExist")
    @ResponseBody
    public String getVerifyCode(HttpServletRequest req, HttpServletResponse resp) throws RequestParamException {
        String username = req.getParameter("username");
        if (username == null)
            throw new RequestParamException("username can not be null");
        if (userService.getUserByUsername(username) != null)
            return "true";
        else
            return "false";
    }

    /**
     * 跳转到注册状态页面
     * @param req
     * @param resp
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/emailActivate")
    public String emailActivate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String fromPage = req.getParameter("from");
        //判断注册状态
        HttpSession session = req.getSession();
        if (session == null){
            resp.sendRedirect(req.getContextPath() + "/static/login" + (fromPage == null ? "" : "?from=" + fromPage));
            return null;
        }
        String email = (String) session.getAttribute("email");
        if (email == null){
            resp.sendRedirect(req.getContextPath() + "/static/login" + (fromPage == null ? "" : "?from=" + fromPage));
            return null;
        }
        User user = userService.getUserByEmail(email);
        if (user != null){
            if (user.getStatus() == 0) {
                session.removeAttribute("nickname");
                Date currentTime = new Date();//获取当前时间
                //验证链接是否过期
                if(currentTime.getTime() - user.getSendActivateCodeTime().getTime() < 24 * 60 * 60 * 1000)
                    session.setAttribute("registerStatus",XmlUtil.getNodeText((String) session.getAttribute("language"),"login.pleaseActivate"));//还没激活，请激活
                else
                    session.setAttribute("registerStatus",XmlUtil.getNodeText((String) session.getAttribute("language"),"login.activateCodeExpire"));//激活邮件已过期，重新发送激活邮件
            } else {
                session.setAttribute("registerStatus",XmlUtil.getNodeText((String) session.getAttribute("language"),"login.activated")); //已激活，请直接登录
            }
            return "email_activate";
        }
        resp.sendRedirect(req.getContextPath() + "/static/login" + (fromPage == null ? "" : "?from=" + fromPage));
        return null;
    }

    /**
     * 激活接口，激活后跳转到首页
     * @param req 请求中必须带“email”和“activateCode”，否则抛出RequestParamException异常
     * @param resp
     * @return
     * @throws RequestParamException
     */
    @RequestMapping(value="/activate")
    public String activate(HttpServletRequest req, HttpServletResponse resp) throws IOException, RequestParamException {
        HttpSession session = req.getSession();
        String email = req.getParameter("email");
        String activateCode = req.getParameter("activateCode");
        if (email == null || activateCode == null)
            throw new RequestParamException("email and activateCode can't be null");
        try {
            userService.activate(email,activateCode,(String) session.getAttribute("language"));
        } catch (ValidateException e) {
            req.getSession().setAttribute("activateError",e.getMessage());
            return "email_activate_fail";
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        String nickname = userService.getUserByEmail(email).getNickname();
        session.setAttribute("nickname", nickname);
        resp.sendRedirect(req.getContextPath() + "/index");
        return null;
    }

    /**
     * 发送激活邮件
     * @param req 请求中必须带“email”，否则抛出RequestParamException异常
     * @param resp
     * @return 成功则返回“success”，失败则返回“fail”
     * @throws RequestParamException
     */
    @RequestMapping(value="/sendActivateEmail")
    @ResponseBody
    public ResponseEntity sendActivateEmail(HttpServletRequest req, HttpServletResponse resp) throws RequestParamException, DocumentException {
        HttpSession session = req.getSession();
        final String email = req.getParameter("email");
        if (email == null || "".equals(email)){
            String error = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.emailCanNotEmpty");
            return new ResponseEntity(SendEmailResult.newFailedInstance(error), HttpStatus.OK);
        }
        try {
            userService.sendActivateEmail(email);
            return new ResponseEntity(SendEmailResult.newSucceedInstance(), HttpStatus.OK);
        } catch (SMTPAddressFailedException e) {
            e.printStackTrace();
            String error = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.emailNotAvailable");
            return new ResponseEntity(SendEmailResult.newFailedInstance(error), HttpStatus.OK);
        }
    }
}
