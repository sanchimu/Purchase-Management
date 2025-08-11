package com.purchase.command.returnInfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.vo.ReceiveInfo;

import mvc.command.CommandHandler;

public class AddReturnInfoHandler implements CommandHandler {

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		
		/*
		 * List<ReceiveInfo> receiveInfoList = returnInfoService.getAllReturnInfo();
		 * req.setAttribute("returnInfoList", returnInfoList);
		 */
		
		return "/WEB-INF/view/ReturnInfoAdd.jsp";
	}

}
