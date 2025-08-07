package com.purchase.command.order;

import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.order.OrderSheetService;
import com.purchase.vo.OrderSheet;

import mvc.command.CommandHandler;

public class AddOrderSheetHandler implements CommandHandler {
	
	private static final String FORM_VIEW = "/WEB-INF/view/AddOrderSheet.jsp"; // ✅ 정답
    private OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (req.getMethod().equalsIgnoreCase("GET")) {
            // 등록 화면 요청
            return FORM_VIEW;
        } else if (req.getMethod().equalsIgnoreCase("POST")) {
            // 폼에서 전달된 파라미터 수집
            String request_id = req.getParameter("request_id");
            String supplier_id = req.getParameter("supplier_id");
            String order_status = req.getParameter("order_status");

            // ID 생성 (예: OR001)
            String order_id = generateOrderId();

            OrderSheet order = new OrderSheet();
            order.setOrder_id(order_id);
            order.setRequest_id(request_id);
            order.setSupplier_id(supplier_id);
            order.setOrder_date(new Date()); // 오늘 날짜
            order.setOrder_status(order_status);

            try {
                service.insert(order); // DB에 저장
                res.sendRedirect("orderSheetList.do"); // 등록 후 목록으로 이동
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                req.setAttribute("error", "등록 중 오류 발생: " + e.getMessage());
                return FORM_VIEW;
            }
        }

        return null;
    }

    // 발주서 ID 생성기
    private String generateOrderId() {
        Random rand = new Random();
        int num = rand.nextInt(900) + 100; // 100~999
        return "OR" + num;
    }
}