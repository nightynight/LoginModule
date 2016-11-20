package com.brokepal.filter;

import com.brokepal.constant.Const;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Administrator on 2016/11/9.
 */
public class LanguageFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)arg0;
        Cookie[] cookies = request.getCookies();
        Cookie cookie = null;
        String language = null; //默认为中文
        if (cookies != null){
            for (int i = 0; i < cookies.length; i++) {
                cookie = cookies[i];
                if (cookie.getName().equals("language")) {
                    language = cookie.getValue();
                    request.getSession().setAttribute("language",language);
                }
            }
        }
        if (language == null)  //当用户的浏览器中没有cookie时，设置为默认语言（中文）
            request.getSession().setAttribute("language", Const.Language.CHINESE);

        arg2.doFilter(arg0, arg1);
    }

    public void init(FilterConfig arg0) throws ServletException {
    }
}
