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

        Map<String, String> searchParams = new HashMap<>(); //Map<>이라는 구조가 키와 값을 한번에 저장할 수 있는거라고 함
        //SearchProductHandler와 동일한것 검색으로 들어온 값들이 여러개인데 그 키와 값을 한번에 저장하기 위해 사용
        //나중에 전달받은 키와 값을 searchParams 한개만 전달해주면 한번에 전달도 가능하니 편함
        
        if(returnId != null && !returnId.trim().isEmpty()) { //입력 받은 productID의 값이 비어있는지를 체크
            searchParams.put("return_id", returnId.trim());  //비어있지 않다면 (!=) 키와 값을 모아두는 searchParams의 공간에 해당 키와 값을 저장
        }
        if(receiveId != null && !receiveId.trim().isEmpty()) {//첫번째에 설명 완료 (반복)
            searchParams.put("receive_id", receiveId.trim());
        }
        if(productId != null && !productId.trim().isEmpty()) {//첫번째에 설명 완료 (반복)
            searchParams.put("product_id", productId.trim());
        }

        // 3. 서비스 호출 → 조건에 맞는 상품 리스트 조회
        List<ReturnInfo> returnInfoList = returmInfoService.getReturnInfosByConditions(searchParams); // 입력받은 값(searchParams를 ReturnInfoService에 있는 getReturnInfosByConditions메서드에 반환

        if (returnInfoList == null || returnInfoList.isEmpty()) {//검색조건에 만족되는 값이
            // 검색 실패 → 전체 리스트 조회
        	req.setAttribute("NoSearchReturnInfoResult", true);
            returnInfoList = returmInfoService.getAllReturnInfo();
        }

        // 4. JSP에 결과 전달
        req.setAttribute("returnInfoList", returnInfoList);//returnInfoList라는 키와 returnInfoList 값을 반환

        
        // 5. 뷰 이름 반환 (productList.jsp 경로)
        //res.sendRedirect(req.getContextPath() + "/listReturnInfos.do");
        req.getRequestDispatcher("/WEB-INF/view/returnInfoList.jsp").forward(req, res); //원래 req에 저장되어 있던 값들을 유지하며 경로의 jsp를 실행
      //redirect는 req에 저장되어 있던 값들을 초기화 후 진행
        return null;

    }
}
