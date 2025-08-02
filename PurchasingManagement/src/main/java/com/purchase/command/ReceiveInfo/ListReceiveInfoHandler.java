package com.purchase.command.ReceiveInfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;

import mvc.command.CommandHandler;

public class ListReceiveInfoHandler implements CommandHandler {

	private ReceiveInfoService receiveInfoService = new ReceiveInfoService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
        List<ReceiveInfo> receiveInfoList = receiveInfoService.getAllReceiveInfos();
        req.setAttribute("receiveInfoList", receiveInfoList);
        return "/WEB-INF/view/ReceiveInfoList.jsp";
	}
}
