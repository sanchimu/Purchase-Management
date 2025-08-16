package com.purchase.command.order;

import com.purchase.service.order.OrderSheetService;
import com.purchase.vo.OrderSheet;
import mvc.command.CommandHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdateOrderSheetHandler implements CommandHandler {

    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
        req.setCharacterEncoding("UTF-8");

        String[] orderIds = req.getParameterValues("orderIds");
        if (orderIds != null && orderIds.length > 0) {
            List<String> allowed = service.getOrderStatusList();
            Set<String> allowSet = new HashSet<>(allowed);

            for (String id : orderIds) {
                if (id == null || id.isBlank()) continue;
                String st = req.getParameter("status_" + id);
                if (st == null || st.isBlank() || !allowSet.contains(st)) continue;

                OrderSheet os = new OrderSheet();
                os.setOrder_id(id);
                os.setOrder_status(st);
                service.updateOrderStatus(os);
            }
        }
        res.sendRedirect(req.getContextPath() + "/orderSheetList.do");
        return null;
    }
}
