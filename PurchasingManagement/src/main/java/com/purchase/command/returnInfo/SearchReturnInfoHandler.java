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

    private final ReturnInfoService returnInfoService = new ReturnInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String returnId  = trim(req.getParameter("return_id"));
        String receiveId = trim(req.getParameter("receive_id"));
        String productId = trim(req.getParameter("product_id"));

        Map<String, String> params = new HashMap<>();
        if (notEmpty(returnId))  params.put("return_id",  returnId);
        if (notEmpty(receiveId)) params.put("receive_id", receiveId);
        if (notEmpty(productId)) params.put("product_id", productId);

        List<ReturnInfo> list = returnInfoService.getReturnInfosByConditions(params);

        // JSP에서 사용: ${returnList}
        req.setAttribute("returnList", list);

        // 결과가 없고, 검색 조건이 하나라도 있었으면 메시지 표시(선택)
        if (list.isEmpty() && !params.isEmpty()) {
            req.setAttribute("message", "조회된 반품 정보가 없습니다.");
        }

        return "/WEB-INF/view/returnInfoList.jsp";
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
    private static boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
