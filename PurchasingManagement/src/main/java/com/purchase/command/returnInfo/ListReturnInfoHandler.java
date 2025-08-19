package com.purchase.command.returnInfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.returnInfo.ReturnInfoService;
import com.purchase.vo.ReturnInfo;

import mvc.command.CommandHandler;

public class ListReturnInfoHandler implements CommandHandler {

	public ReturnInfoService returnInfoService = new ReturnInfoService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
        List<ReturnInfo> returnInfoList = returnInfoService.getAllReturnInfo();
        req.setAttribute("returnInfoList", returnInfoList);
        return "/WEB-INF/view/returnInfoList.jsp";
	}
}


