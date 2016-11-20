package com.brokepal.controller.pages.staticController;

import com.brokepal.exception.RequestParamException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2016/11/9.
 */
@Controller
@RequestMapping(value="/static")
public class LanguageController {

    /**
     * 设置语言，必须传入一个language参数，否则会抛出异常RequestParamException
     * @param req
     * @param resp
     * @return
     * @throws RequestParamException
     */
    @RequestMapping(value="/language")
    @ResponseBody
    public String getVerifyCode(HttpServletRequest req, HttpServletResponse resp) throws RequestParamException {
        String language = req.getParameter("language");
        if (language == null)
            throw new RequestParamException("language can not be null");
        req.getSession().setAttribute("language",language);
        return "success";
    }
}
