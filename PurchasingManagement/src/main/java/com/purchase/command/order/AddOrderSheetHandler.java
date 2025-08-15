package com.purchase.command.order;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.order.OrderSheetService;
import com.purchase.vo.OrderSheet;

import mvc.command.CommandHandler;

public class AddOrderSheetHandler implements CommandHandler {

    private static final String FORM_VIEW = "/WEB-INF/view/AddOrderSheet.jsp";
    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // 드롭다운 옵션 바인딩
            req.setAttribute("orderStatusList", service.getOrderStatusList());
            return FORM_VIEW;
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            req.setCharacterEncoding("UTF-8");

            String request_id  = trim(req.getParameter("request_id"));
            String supplier_id = trim(req.getParameter("supplier_id"));
            String order_status = trim(req.getParameter("order_status"));

            // 필수값 체크
            if (isEmpty(request_id) || isEmpty(supplier_id)) {
                req.setAttribute("error", "요청ID와 공급업체ID는 필수입니다.");
                req.setAttribute("orderStatusList", service.getOrderStatusList());
                return FORM_VIEW;
            }

            // 상태값 검증 (허용 세트)
            List<String> allowed = service.getOrderStatusList(); // 요청/승인/발주/부분입고/완료/취소/보류
            if (isEmpty(order_status)) order_status = "요청";     // 기본값
            if (!allowed.contains(order_status)) {
                req.setAttribute("error", "허용되지 않은 상태값입니다.");
                req.setAttribute("orderStatusList", allowed);
                return FORM_VIEW;
            }

            // 시퀀스로 안전한 ID 발급 (예: OR001, OR002…)
            String order_id = service.generateNextOrderId();

            OrderSheet order = new OrderSheet();
            order.setOrder_id(order_id);
            order.setRequest_id(request_id);
            order.setSupplier_id(supplier_id);
            order.setOrder_date(new Date());     // 오늘
            order.setOrder_status(order_status); // 검증된 값

            try {
                service.insert(order);
                res.sendRedirect(req.getContextPath() + "/orderSheetList.do");
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("error", "등록 중 오류 발생: " + e.getMessage());
                req.setAttribute("orderStatusList", allowed);
                return FORM_VIEW;
            }
        }

        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }

    private static String trim(String s){ return s==null?null:s.trim(); }
    private static boolean isEmpty(String s){ return s==null || s.trim().isEmpty(); }
}
