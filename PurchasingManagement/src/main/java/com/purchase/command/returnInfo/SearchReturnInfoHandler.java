package com.purchase.command.returnInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.returnInfo.ReturnInfoService;
import com.purchase.vo.ReturnInfo;

import mvc.command.CommandHandler;

public class SearchReturnInfoHandler implements CommandHandler {

	private ReturnInfoService returmInfoService = new ReturnInfoService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		String returnId = req.getParameter("return_id");
        String receiveId = req.getParameter("receive_id");
        String productId = req.getParameter("product_id");

        Map<String, String> searchParams = new HashMap<>();
        if(returnId != null && !returnId.trim().isEmpty()) {
            searchParams.put("return_id", returnId.trim());
        }
        if(receiveId != null && !receiveId.trim().isEmpty()) {
            searchParams.put("receive_id", receiveId.trim());
        }
        if(productId != null && !productId.trim().isEmpty()) {
            searchParams.put("product_id", productId.trim());
        }

        // 3. 서비스 호출 → 조건에 맞는 상품 리스트 조회
        List<ReturnInfo> returnInfoList = returmInfoService.getReturnInfosByConditions(searchParams);

        // 4. JSP에 결과 전달
        req.setAttribute("returnInfoList", returnInfoList);

        // 5. 뷰 이름 반환 (productList.jsp 경로)
        return "/WEB-INF/view/ReturnInfoList.jsp";
    }
}
