package com.purchase.command.ReceiveInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;

import mvc.command.CommandHandler;

import java.text.SimpleDateFormat;

public class AddReceiveInfoHandler implements CommandHandler {

    private ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        if (req.getMethod().equalsIgnoreCase("GET")) {
            // 등록 폼 JSP 경로
            return "/WEB-INF/view/receiveInfoForm.jsp";
        } else if (req.getMethod().equalsIgnoreCase("POST")) {
            req.setCharacterEncoding("UTF-8");

            // 클라이언트에서 receive_id 파라미터 받지 않음
            String orderId = req.getParameter("order_id");
            String productId = req.getParameter("product_id");
            String quantityStr = req.getParameter("quantity");
            String receiveDateStr = req.getParameter("receive_date");

            int quantity = (quantityStr != null && !quantityStr.isEmpty()) ? Integer.parseInt(quantityStr) : 0;

            ReceiveInfo receiveInfo = new ReceiveInfo();
            // receive_id는 시퀀스에서 자동 생성하므로 세팅하지 않음
            receiveInfo.setOrder_id(orderId);
            receiveInfo.setProduct_id(productId);
            receiveInfo.setQuantity(quantity);

            if (receiveDateStr != null && !receiveDateStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                receiveInfo.setReceive_date(sdf.parse(receiveDateStr));
            }

            service.addReceiveInfo(receiveInfo);

            // 등록 후 목록으로 리다이렉트
            res.sendRedirect(req.getContextPath() + "/listReceiveInfos.do");
            return null;
        }

        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }
}
