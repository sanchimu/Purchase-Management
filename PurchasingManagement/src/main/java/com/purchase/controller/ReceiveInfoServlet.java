package com.purchase.controller;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/receiveInfo.do") 
public class ReceiveInfoServlet extends HttpServlet {
    private ReceiveInfoService service = new ReceiveInfoService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        
        String orderId = req.getParameter("order_id");
        String productId = req.getParameter("product_id");
        int quantity = Integer.parseInt(req.getParameter("quantity"));

     
        ReceiveInfo vo = new ReceiveInfo();
        vo.setOrder_id(orderId);      
        vo.setProduct_id(productId);  
        vo.setQuantity(quantity);

        try {
 
            service.addReceiveInfo(vo);


            res.sendRedirect("receiveInfoList.do");
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
