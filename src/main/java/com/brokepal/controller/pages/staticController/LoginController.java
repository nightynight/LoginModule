package com.brokepal.controller.pages.staticController;

import com.brokepal.entity.User;
import com.brokepal.service.UserService;
import com.brokepal.utils.MD5Util;
import com.brokepal.utils.RSACryptoUtil;
import com.brokepal.utils.VerifyCode;
import com.brokepal.utils.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.security.interfaces.RSAPrivateKey;

@Controller
@RequestMapping(value="/static")
public class LoginController {
	private String str_privateKey;
	private String verifyCode;

	@Autowired
	private UserService userService;

	@RequestMapping(value="/login")
	public String Login(HttpServletRequest req,HttpServletResponse resp) throws Exception {
		HttpSession session = req.getSession();
		session.removeAttribute("loginFailInfo");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String loginTimestamp = req.getParameter("loginTimestamp");
		int failedCount = session.getAttribute("failedCount") == null ? 0 : Integer.parseInt(session.getAttribute("failedCount").toString());
		session.removeAttribute("nickname");
		do {
			//1.判断是请求页面还是提交数据
			if (username == null){
				break;
			}

			//2.判断是否是F5刷新引起的表单重复提交
			String oldTimestamp = session.getAttribute("loginTimestamp") == null ? "" : session.getAttribute("loginTimestamp").toString();	//上次提交请求的时间
			if (oldTimestamp.equals(loginTimestamp)) //对比两次提交时间，不一样则表示不是重复提交
				break;
			else
				session.setAttribute("loginTimestamp",loginTimestamp);

			//3.是否需要验证码
			if (failedCount <= 2) { //当第n次尝试登录时，failedCount为n-1
				if (verifyUsernameAndPassword(req,resp))	//处理用户数据
					return null;
			} else {	//当登录失败次数大于3次时，开始使用验证码
				String verifyCode=req.getParameter("verifyCode");
				if (verifyCode == null || !verifyCode.equalsIgnoreCase(session.getAttribute("verifyCode").toString())) {
					session.setAttribute("loginFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"), "login.wrongVerifyCode"));
					break;
				}
				if (verifyUsernameAndPassword(req,resp))
					return null;
			}
		} while (false);

		RSACryptoUtil.KeyPairOfString keyPairOfString = RSACryptoUtil.makeBothKeyOfString();
//		System.out.println("公钥："+ keyPairOfString.getPublicKey());
//		System.out.println("私钥："+ keyPairOfString.getPrivateKey());
		String str_publicKey = keyPairOfString.getPublicKey();
		session.setAttribute("publicKey", str_publicKey.replace("\n",""));	//将公钥放入session，发往客户端，将用户的密码加密
		str_privateKey = keyPairOfString.getPrivateKey();		//privateKey用于将接受到的加密后的密码进行解密
		if (session.getAttribute("failedCount") == null)
			session.setAttribute("failedCount",0);
		return "login";
	}

	@RequestMapping(value="/logout")
	public String Logout(HttpServletRequest req,HttpServletResponse resp) throws Exception {
		HttpSession session=req.getSession();
		session.removeAttribute("nickname");
		resp.sendRedirect(req.getContextPath()+"/static/login");
		return null;
	}

	/**
	 * 生成验证码图片，将其写到输出流，客户端访问该接口可直接得到一张验证码图片
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/verifyCodeImage")
	public String getVerifyImage(HttpServletRequest req,HttpServletResponse resp) throws Exception {
		VerifyCode vf = new VerifyCode();
		BufferedImage image = vf.createImage();	//生成验证码图片
		verifyCode = vf.getText();	//记录验证码
		req.getSession().setAttribute("verifyCode",verifyCode); //将验证码写入session
		VerifyCode.output(image, resp.getOutputStream());	//把图片相应给客户端
		return null;
	}

	/**
	 * 验证输入的验证码是否正确
	 * @param req 请求中必须带“verifyCode”
	 * @param resp
	 * @return 正确则返回“succeed”，错误则返回“failed”
	 * @throws Exception
	 */
	@RequestMapping(value="/verifyCode")
	@ResponseBody
	public String getVerifyCode(HttpServletRequest req,HttpServletResponse resp) throws Exception {
		String verifyCode = req.getParameter("verifyCode");
		if (verifyCode != null && verifyCode.equalsIgnoreCase(this.verifyCode))
			return "succeed";
		else
			return "failed";
	}

	/**
	 * 验证用户名、密码是否正确，如果正确，则直接跳转到相应页面，并返回true；否则不跳转，返回false
	 * PS: 用户名也可以是邮箱
	 * @param req
	 * @param resp
	 * @return 如果正确，返回true，否则返回false
	 * @throws Exception
	 */
    private boolean verifyUsernameAndPassword(HttpServletRequest req,HttpServletResponse resp) throws Exception {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		HttpSession session=req.getSession();
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) RSACryptoUtil.getPrivateKey(str_privateKey);	//得到私钥
		String srcUsername = RSACryptoUtil.RSADecodeWithPrivateKey(rsaPrivateKey,username);	//解密用户名
		if (!("".equals(srcUsername))){
			//处理由登录页面发过来的数据
			User user = userService.getUserByUsername(srcUsername);
			if (user == null)
				user = userService.getUserByEmail(srcUsername);//先认为username为用户名，如果没有查到，再将其看作邮箱再查询一遍
			if (user == null)
					session.setAttribute("loginFailInfo",XmlUtil.getNodeText((String) session.getAttribute("language"),"login.accountNotExist"));	//账号不存在
			else {
				String srcPassword = RSACryptoUtil.RSADecodeWithPrivateKey(rsaPrivateKey,password);	//解密密码
				String salt = user.getSalt();
				String passwordMD5 = MD5Util.string2MD5(MD5Util.string2MD5(srcPassword) + salt);	//用MD5加密密码，数据库中存的就是用户密码经过MD5加密后的字符串
				if (user.getPassword().equals(passwordMD5)) {
					String fromPage = req.getParameter("from");
					if (user.getStatus() == 0) {//如果还没激活，设置email，到注册状态页面，根据email查询状态信息
						session.setAttribute("email",user.getEmail());
						if (fromPage == null || "".equals(fromPage))
							resp.sendRedirect(req.getContextPath()+"/static/emailActivate");
						else
							resp.sendRedirect(req.getContextPath()+"/static/emailActivate?from=" + fromPage);
					} else { //如果已激活，则直接登录，并设置nickname
						session.setAttribute("nickname", user.getNickname());
						if (fromPage == null || "".equals(fromPage))
							resp.sendRedirect(req.getContextPath() + "/index");
						else
							resp.sendRedirect(req.getContextPath() + "/" + fromPage);
					}
					session.removeAttribute("loginFailInfo");
					return true;
				} else {	//如果密码错误
					int failedCount = session.getAttribute("failedCount") == null ? 0 : Integer.parseInt(session.getAttribute("failedCount").toString());
					session.setAttribute("failedCount", ++failedCount);
					session.setAttribute("loginFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"),"login.wrongPassword"));
				}
			}
		} else {
			session.setAttribute("loginFailInfo", XmlUtil.getNodeText((String) session.getAttribute("language"),"login.usernameCanNotEmpry"));
		}
		return false;
	}
}