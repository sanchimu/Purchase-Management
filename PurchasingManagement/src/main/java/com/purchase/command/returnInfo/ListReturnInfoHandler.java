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
        List<ReturnInfo> returnInfoList = returnInfoService.getAllReturnInfo();//단순히 반품 정보 리스트를 띄우는 것이어서 List형식의 returnInfoList에 반품 정보를 넣고 
        req.setAttribute("returnInfoList", returnInfoList); // 키값을 세팅
        return "/WEB-INF/view/returnInfoList.jsp";
	}
}


