package com.purchase.command.returnInfo;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.service.returnInfo.ReturnInfoService;
import com.purchase.vo.ReturnInfo;

import mvc.command.CommandHandler;

public class ReturnProcessHandler implements CommandHandler {

	private ReturnInfoService returnInfoService = new ReturnInfoService();

	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		
		String receive_id= req.getParameter("receiveId");
		String product_id = returnInfoService.getProductIdByReceiveId(receive_id);
		String quantityStr = req.getParameter("quantity"); 
		String reason = req.getParameter("reason");
		Date return_date = new Date();
		
		if(receive_id == null || receive_id.length() == 0) {
			req.setAttribute("error",	"반품할 항목을 선택하세요");
			return "/WEB-INF/view/ReturnInfoAdd.jsp";
		}else 	if (quantityStr == null || quantityStr.trim().isEmpty()) {
			req.setAttribute("error",	"반품 이유를 입력하세요");
			return "/WEB-INF/view/ReturnInfoAdd.jsp";
		}
		
		int quantity;
		
		try {
			quantity = Integer.parseInt(quantityStr);
		}catch(NumberFormatException e) {
			req.setAttribute("error",	"정확한 반품 수량을 기재해주세요");
			return "/WEB-INF/view/ReturnInfoAdd.jsp";
		}
		
		ReturnInfo returnInfo = new ReturnInfo(null, receive_id, product_id, quantity, reason, return_date);
		returnInfoService.addReturnInfo(returnInfo);
		
		   req.setAttribute("success", true);

		    
		   res.sendRedirect(req.getContextPath() + "/returnInfoList.do"); // 그냥 경로 넣으면 핸들러를 안타서 데이터를 못불러옴, 이렇게 리다이렉트 형식으로 다시 해야 핸들러를탐
		   return null;
	}

}
