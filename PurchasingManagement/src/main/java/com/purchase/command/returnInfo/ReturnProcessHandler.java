package com.purchase.command.returnInfo;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.returnInfo.ReturnInfoService;
import com.purchase.vo.ReturnInfo;

import mvc.command.CommandHandler;

public class ReturnProcessHandler implements CommandHandler {

    private final ReturnInfoService returnInfoService = new ReturnInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        // 라디오 name 불일치 대비: 둘 다 받아보고 있으면 하나를 사용
        String receive_id = req.getParameter("receiveId");
        if (receive_id == null || receive_id.isEmpty()) {
            receive_id = req.getParameter("receive_id");
        }

        String quantityStr = req.getParameter("quantity");
        String reason      = req.getParameter("reason");

        if (receive_id == null || receive_id.trim().isEmpty()) {
            req.setAttribute("error", "반품할 입고 항목을 선택하세요.");
            return "/WEB-INF/view/returnInfoAdd.jsp";
        }
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            req.setAttribute("error", "반품 수량을 입력하세요.");
            return "/WEB-INF/view/returnInfoAdd.jsp";
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            req.setAttribute("error", "반품 수량은 1 이상의 숫자여야 합니다.");
            return "/WEB-INF/view/returnInfoAdd.jsp";
        }

        // 필요 시 reason 필수 처리
        if (reason == null || reason.trim().isEmpty()) {
            req.setAttribute("error", "반품 사유를 입력하세요.");
            return "/WEB-INF/view/returnInfoAdd.jsp";
        }

        // 입고ID로 상품ID 조회
        String product_id = returnInfoService.getProductIdByReceiveId(receive_id);
        Date return_date = new Date();

        ReturnInfo vo = new ReturnInfo(null, receive_id, product_id, quantity, reason, return_date);
        returnInfoService.addReturnInfo(vo);

        // 목록으로 리다이렉트 (핸들러를 태워야 테이블이 채워짐)
        res.sendRedirect(req.getContextPath() + "/returnInfoList.do");
        return null;
    }
}
