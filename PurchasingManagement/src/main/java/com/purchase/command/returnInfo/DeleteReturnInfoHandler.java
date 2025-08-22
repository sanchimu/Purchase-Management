package com.purchase.command.returnInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.returnInfo.ReturnInfoService;

import mvc.command.CommandHandler;

public class DeleteReturnInfoHandler implements CommandHandler {

	private ReturnInfoService returnInfoService = new ReturnInfoService(); 
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		
		// TODO Auto-generated method stub
		 String[] returnInfoIds = req.getParameterValues("returnInfoIds");

	        if (returnInfoIds != null && returnInfoIds.length > 0) {
	        	returnInfoService.deleteReturnInfo(returnInfoIds);
	            req.setAttribute("deletedCount", returnInfoIds.length);
	            res.sendRedirect(req.getContextPath() + "/returnInfoList.do");
	    	    return null;
	        } else {
	            req.setAttribute("error", "삭제할 상품을 선택하세요.");
	            return "/WEB-INF/view/deleteFail.jsp";
	        }
	    
	}

}
