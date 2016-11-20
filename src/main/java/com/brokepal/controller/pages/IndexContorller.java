package com.brokepal.controller.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class IndexContorller {
	//hello world例子
	@RequestMapping(value="/index")
	public ModelAndView index(HttpServletRequest req, HttpServletResponse resp){
		ModelAndView mv=new ModelAndView();
		mv.addObject("msg",req.getSession().getAttribute("nickname"));
		mv.setViewName("main-pages/index");
		return mv;
	}

	@RequestMapping(value="/index2")
	public ModelAndView index2(HttpServletRequest req, HttpServletResponse resp){
		ModelAndView mv=new ModelAndView();
		mv.addObject("msg",req.getSession().getAttribute("nickname"));
		mv.setViewName("main-pages/index2");
		return mv;
	}
}
