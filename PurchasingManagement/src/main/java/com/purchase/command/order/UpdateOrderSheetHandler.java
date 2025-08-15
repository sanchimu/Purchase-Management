package com.purchase.command.order;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.order.OrderSheetService;
import com.purchase.vo.OrderSheet;

import mvc.command.CommandHandler;

public class UpdateOrderSheetHandler implements CommandHandler {

    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }

        req.setCharacterEncoding("UTF-8");

        // 선택된 행만 업데이트 (없으면 아무 것도 안 함)
        String[] orderIds = req.getParameterValues("orderIds");
        if (orderIds == null || orderIds.length == 0) {
            // 선택 없으면 목록으로
            res.sendRedirect(req.getContextPath() + "/orderSheetList.do");
            return null;
        }

        // 허용 상태 세트 (DB CHECK와 동일)
        List<String> allowedList = service.getOrderStatusList();
        Set<String> allowed = new HashSet<>(allowedList);

        int updated = 0;
        for (String oid : orderIds) {
            if (oid == null || oid.trim().isEmpty()) continue;

            String paramName = "status_" + oid;
            String status = req.getParameter(paramName);
            if (status == null || status.trim().isEmpty()) continue;

            if (!allowed.contains(status)) {
                // 허용 외 값이면 스킵 (DB CHECK 위반 방지)
                continue;
            }

            OrderSheet os = new OrderSheet();
            os.setOrder_id(oid);
            os.setOrder_status(status);
            service.updateOrderStatus(os);
            updated++;
        }

        // 저장 후 목록으로
        res.sendRedirect(req.getContextPath() + "/orderSheetList.do");
        return null;
    }
}
