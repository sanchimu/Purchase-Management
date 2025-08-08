package com.purchase.controller;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/receiveInfo.do")  // ✅ URL 매핑을 .do 형식으로 변경
public class ReceiveInfoServlet extends HttpServlet {
    private ReceiveInfoService service = new ReceiveInfoService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        // 입력값 파라미터 수집
        String orderId = req.getParameter("order_id");
        String productId = req.getParameter("product_id");
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        // VO 객체 생성
        ReceiveInfo vo = new ReceiveInfo();
        vo.setOrderId(orderId);
        vo.setProductId(productId);
        vo.setQuantity(quantity);

        try {
            // 서비스 로직 실행
            service.addReceiveInfo(vo);

            // 등록 성공 시 목록 페이지로 리다이렉트
            res.sendRedirect("receiveInfoList.do"); // ✅ 목록도 .do로 리다이렉트
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "DB 저장 중 오류가 발생했습니다.");
            req.getRequestDispatcher("/WEB-INF/view/receiveInfoForm.jsp").forward(req, res);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/view/receiveInfoForm.jsp").forward(req, res);
    }
}
