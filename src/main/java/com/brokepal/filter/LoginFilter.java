package com.brokepal.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 对于带有"static/"的controller请求不进行过滤
 */
public class LoginFilter implements Filter {
	private FilterConfig config;

	public void destroy() {

	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req=(HttpServletRequest) arg0;
		HttpServletResponse resp=(HttpServletResponse) arg1;
		HttpSession session=req.getSession();
		
		//设置编码
		String charset=config.getInitParameter("charset");
		if (charset==null) {
			charset="UTF-8";//设置默认值
		}
		req.setCharacterEncoding(charset);
		
		//排除在配置文件中配置的不需要过滤的请求
		String noLoginPaths= config.getInitParameter("noLoginPaths");
		if (noLoginPaths!=null) {
			String[] strArray=noLoginPaths.split(",");
			for (int i = 0; i < strArray.length; i++) {
				if (strArray[i]==null || "".equals(strArray[i].trim())) continue;
				if (req.getRequestURI().indexOf(strArray[i].trim())!=-1) {
					arg2.doFilter(arg0, arg1);
					return;
				}
			}
		}
		//判断是否已登录
		if (session.getAttribute("nickname")!=null) {
			arg2.doFilter(arg0, arg1);
		}else {
			//从URL中获取当前页面
			String from = req.getRequestURI();	//获取到的结果为"/"或"/..."
			from = from.substring(1,from.length());
			if ("".equals(from))
				resp.sendRedirect(req.getContextPath() + "/static/login");
			else
				resp.sendRedirect(req.getContextPath() + "/static/login?from=" + from);
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
		// 获取在配置文件中设置的init-param
		this.config=arg0;
	}
}
