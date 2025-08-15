package com.purchase.command.ReceiveInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;

import mvc.command.CommandHandler;

public class AddReceiveInfoHandler implements CommandHandler {

    private final ReceiveInfoService service = new ReceiveInfoService();
    private static final String FORM = "/WEB-INF/view/receiveInfoForm.jsp";

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // 상태 드롭다운 데이터
            req.setAttribute("receiveStatusList", service.getReceiveStatusList());
            return FORM;
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            req.setCharacterEncoding("UTF-8");

            String orderId        = req.getParameter("order_id");
            String productId      = req.getParameter("product_id");
            String quantityStr    = req.getParameter("quantity");
            String receiveDateStr = req.getParameter("receive_date");
            String statusParam    = req.getParameter("receive_status"); // 선택 사항

            int quantity = (quantityStr != null && !quantityStr.isEmpty())
                    ? Integer.parseInt(quantityStr) : 0;

            ReceiveInfo vo = new ReceiveInfo();
            vo.setOrder_id(orderId);
            vo.setProduct_id(productId);
            vo.setQuantity(quantity);

            if (receiveDateStr != null && !receiveDateStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                vo.setReceive_date(sdf.parse(receiveDateStr));
            }

            // 허용된 상태만 세팅 (그 외는 null → DAO에서 '검수중' 기본값 적용)
            List<String> allowed = service.getReceiveStatusList();
            if (statusParam != null && allowed.contains(statusParam)) {
                vo.setReceive_status(statusParam);
            } else {
                vo.setReceive_status(null);
            }

            // 등록 (DAO가 새 receive_id를 vo에 세팅함)
            service.addReceiveInfo(vo);

            // 완료 후 목록으로
            res.sendRedirect(req.getContextPath() + "/listReceiveInfos.do");
            return null;
        }

        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }
}
