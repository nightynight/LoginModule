package com.brokepal.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(filterName="ErrorFilter",value={"/error.jsp"},dispatcherTypes={DispatcherType.ERROR})
public class ErrorFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		arg2.doFilter(arg0, arg1);
	}

	public void init(FilterConfig arg0) throws ServletException {
	}
}
