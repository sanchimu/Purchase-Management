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
		 String[] returnInfoIds = req.getParameterValues("returnInfoIds"); //id를 여러개 넘기는 이유는 한번에 여러개의 id를 보내 여러개의 반품 정보를 삭제하기 위함

	        if (returnInfoIds != null && returnInfoIds.length > 0) {
	        	returnInfoService.deleteReturnInfo(returnInfoIds);
	            req.setAttribute("deletedCount", returnInfoIds.length);
	            res.sendRedirect(req.getContextPath() + "/returnInfoList.do"); //삭제 처리한 후 새로고침
	    	    return null;
	        } else {//else의 경우 선택한 id 즉 반품 목록이 없는경우 이지만 현재 jsp상에서 삭제품이 없을경우 프로세스 실행을 강제로 막고있어서 반품 목록이 없는 경우로 넘어오지 못하는 상태
	        	res.sendRedirect(req.getContextPath() + "/returnInfoList.do");
	            return null;
	        }
	    
	}

}
