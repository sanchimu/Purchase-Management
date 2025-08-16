package com.purchase.command.order;

import com.purchase.service.order.OrderSheetService;
import mvc.command.CommandHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OrderSheetRowStatusResumeHandler implements CommandHandler {
    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
        String[] ids = req.getParameterValues("orderIds");
        service.setRowStatusBulk(ids, "A");
        res.sendRedirect(req.getContextPath() + "/orderSheetList.do");
        return null;
    }
}
