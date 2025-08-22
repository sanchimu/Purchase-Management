package com.purchase.command.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.request.PurchaseRequestService;
import com.purchase.vo.PurchaseRequest;
import mvc.command.CommandHandler;

public class PurchaseRequestQtyHandler implements CommandHandler {

    private final PurchaseRequestService service = new PurchaseRequestService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
    	
    	// GET: 수정 폼 표시(단건 조회) 
    	// GET：修正フォーム表示（単件照会）
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            String requestId = req.getParameter("request_id");
            PurchaseRequest pr = service.getById(requestId);
            req.setAttribute("req", pr);
            return "/WEB-INF/view/purchaseRequestQtyForm.jsp";
        }

        // POST(수량 저장 처리) 
        // POST(数量の保存処理)
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String requestId = req.getParameter("request_id");
            int quantity = Integer.parseInt(req.getParameter("quantity"));

            service.updatePurchaseRequestQuantity(requestId, quantity);

            req.setAttribute("successMsg", "数量を修正しました。");

            return "/WEB-INF/view/purchaseRequestQtyForm.jsp";
        }
        res.setStatus(405);
        return null;
    }
}
