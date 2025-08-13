package com.purchase.command.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.request.PurchaseRequestService;

import mvc.command.CommandHandler;

public class DeletePurchaseRequestHandler implements CommandHandler {

    private final PurchaseRequestService service = new PurchaseRequestService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (!req.getMethod().equalsIgnoreCase("POST")) {
            // GET 등은 목록으로
            res.sendRedirect(req.getContextPath() + "/requestList.do");
            return null;
        }

        String[] ids = req.getParameterValues("request_id");

        if (ids == null || ids.length == 0) {
            req.getSession().setAttribute("message", "선택된 항목이 없습니다.");
            res.sendRedirect(req.getContextPath() + "/requestList.do");
            return null;
        }

        int deleted = service.removePurchaseRequests(ids);

        req.getSession().setAttribute("message", deleted + "건 삭제되었습니다.");
        res.sendRedirect(req.getContextPath() + "/requestList.do");
        return null;
    }
}
