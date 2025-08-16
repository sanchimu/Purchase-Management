package com.purchase.command.order;

import com.purchase.dto.OrderSheetDTO;
import com.purchase.service.order.OrderSheetService;
import com.purchase.vo.OrderSheet;
import mvc.command.CommandHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public class AddOrderSheetHandler implements CommandHandler {

    private static final String FORM_VIEW = "/WEB-INF/view/AddOrderSheet.jsp";
    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // 드롭다운 옵션 + 상태리스트
            List<OrderSheetDTO> options = service.getRequestOptions();
            req.setAttribute("requestOptions", options);
            req.setAttribute("orderStatusList", service.getOrderStatusList());
            return FORM_VIEW;
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            req.setCharacterEncoding("UTF-8");

            String requestId   = trim(req.getParameter("request_id"));
            String orderStatus = trim(req.getParameter("order_status"));

            if (isEmpty(requestId)) {
                req.setAttribute("error", "요청ID는 필수입니다.");
                req.setAttribute("requestOptions", service.getRequestOptions());
                req.setAttribute("orderStatusList", service.getOrderStatusList());
                return FORM_VIEW;
            }

            // 서버에서 supplier_id 재계산 (신뢰성)
            String supplierId = service.findSupplierIdByRequestId(requestId);
            if (supplierId == null) {
                req.setAttribute("error", "요청ID에 해당하는 공급업체를 찾을 수 없습니다.");
                req.setAttribute("requestOptions", service.getRequestOptions());
                req.setAttribute("orderStatusList", service.getOrderStatusList());
                return FORM_VIEW;
            }

            if (isEmpty(orderStatus)) orderStatus = "요청";

            String orderId = service.generateNextOrderId();

            OrderSheet os = new OrderSheet();
            os.setOrder_id(orderId);
            os.setRequest_id(requestId);
            os.setSupplier_id(supplierId);
            os.setOrder_date(new Date());
            os.setOrder_status(orderStatus);
            os.setRow_status("A"); // 기본 진행

            try {
                service.insert(os);
                res.sendRedirect(req.getContextPath() + "/orderSheetList.do");
                return null;
            } catch (RuntimeException e) {
                req.setAttribute("error", "등록 중 오류: " + e.getMessage());
                req.setAttribute("requestOptions", service.getRequestOptions());
                req.setAttribute("orderStatusList", service.getOrderStatusList());
                return FORM_VIEW;
            }
        }

        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }

    private static String trim(String s){ return s==null?null:s.trim(); }
    private static boolean isEmpty(String s){ return s==null || s.trim().isEmpty(); }
}
