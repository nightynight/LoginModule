package com.brokepal.controller.pages.staticController;

import com.brokepal.dto.SendEmailResult;
import com.brokepal.entity.User;
import com.brokepal.exception.RequestParamException;
import com.brokepal.service.UserService;
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
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;

/**
 * Created by Administrator on 2016/11/9.
 */
@Controller
@RequestMapping(value="/static")
public class ResetPassword {
    private String str_privateKey = null;

    @Autowired
    private UserService userService;

    @RequestMapping(value="/resetPassword")
    public String Login(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session=req.getSession();
        session.removeAttribute("resetPasswordFailInfo");
        String emial = req.getParameter("email");
        String password = req.getParameter("password");
        String passwordConfirm = req.getParameter("passwordConfirm");
        String validateCode = req.getParameter("validateCode");
        String resetPasswordTimestamp = req.getParameter("resetPasswordTimestamp");
        do {
            //1.判断是否是F5刷新引起的表单重复提交
            String oldTimestamp = session.getAttribute("resetPasswordTimestamp") == null ? "" : session.getAttribute("resetPasswordTimestamp").toString();
            if (oldTimestamp.equals(resetPasswordTimestamp))
                break;
            else
                session.setAttribute("resetPasswordTimestamp", resetPasswordTimestamp);

            //2.验证邮箱
            if (emial ==null || "".equals(emial))
                break;
            User user = userService.getUserByEmail(emial);
            if (user == null) {
                session.setAttribute("resetPasswordFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"), "login.emailNotUsed"));
                break;
            }
            session.setAttribute("email",emial);

            //3.判断验证码是否过期
            Date currentTime = new Date();//获取当前时间
            if(currentTime.getTime() - user.getSendValidateCodeTime().getTime() > 15 * 60 * 1000) {
                session.setAttribute("resetPasswordFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"), "login.validateCodeExpire"));
                break;
            }

            //4.判断验证码是否正确
            String correctValidateCode = user.getValidateCode();
            if (correctValidateCode == null) {
                session.setAttribute("resetPasswordFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"), "login.validateCodeNotAvailable"));
                break;
            }
            if ("".equals(validateCode) || !validateCode.equals(correctValidateCode)){
                session.setAttribute("resetPasswordFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"), "login.validateCodeNotCorrect"));
                break;
            }

            //5.判断两次密码是否相同
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) RSACryptoUtil.getPrivateKey(str_privateKey);
            String srcPassword = RSACryptoUtil.RSADecodeWithPrivateKey(rsaPrivateKey,password);
            String srcPasswordConfirm = RSACryptoUtil.RSADecodeWithPrivateKey(rsaPrivateKey,passwordConfirm);
            if (!srcPassword.equals(srcPasswordConfirm)) { //两次输入密码是否相同
                session.setAttribute("resetPasswordFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"), "login.passwordNotSame"));
                break;
            }

            //6.验证密码的有效性
            if (srcPassword.length() < 0) {  //TODO 验证密码是否符合规范
                session.setAttribute("resetPasswordFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"), "login.passwordNotAvailable"));
                break;
            }

            //通过所有验证，重置密码
            String salt = user.getSalt();
            String passwordMD5 = MD5Util.string2MD5(MD5Util.string2MD5(srcPassword) + salt);
            user.setPassword(passwordMD5);
            userService.updateUser(user);


            //跳转页面
            session.setAttribute("nickname",user.getNickname());
            String fromPage = req.getParameter("from");
            if (fromPage == null || "".equals(fromPage))
                resp.sendRedirect(req.getContextPath() + "/index");
            else
                resp.sendRedirect(req.getContextPath() + "/" + fromPage);
            session.removeAttribute("resetPasswordFailInfo");
            return null;
        } while (false);

        RSACryptoUtil.KeyPairOfString keyPairOfString = RSACryptoUtil.makeBothKeyOfString();
        String str_publicKey = keyPairOfString.getPublicKey();	//publicKey会发往客户端，将用户的密码加密
        session.setAttribute("publicKey", str_publicKey.replace("\n",""));	//将公钥放入session，发往客户端，将用户的密码加密
        str_privateKey = keyPairOfString.getPrivateKey();		//privateKey用于将接受到的加密后的密码进行解密
        return "reset_password";
    }

    /**
     * 发送验证码邮件
     * @param req 请求中必须带“email”，且不能为空
     * @param resp
     * @return json对象 成功则返回{"result":"succeed"}，失败则返回{"result":"failed","errorInfo":错误信息}
     */
    @RequestMapping(value="/sendValidateEmail")
    @ResponseBody
    public ResponseEntity sendValidateEmail(HttpServletRequest req, HttpServletResponse resp) throws DocumentException {
        HttpSession session = req.getSession();
        resp.setContentType("text/html;charset=utf-8");
        final String email = req.getParameter("email");
        if (email == null || "".equals(email)){
            String error = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.emailCanNotEmpty");
            return new ResponseEntity(SendEmailResult.newFailedInstance(error), HttpStatus.OK);
        }
        if (userService.getUserByEmail(email) == null) {
            String error = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.emailNotUsed");
            return new ResponseEntity(SendEmailResult.newFailedInstance(error), HttpStatus.OK);
        }

        new Thread(new Runnable() {
            public void run() {
                try {
                    userService.sendValidateCodeEmial(email);
                } catch (SMTPAddressFailedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return new ResponseEntity(SendEmailResult.newSucceedInstance(), HttpStatus.OK);
    }
}
