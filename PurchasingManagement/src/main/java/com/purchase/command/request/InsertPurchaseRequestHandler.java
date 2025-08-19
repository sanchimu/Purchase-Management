// src/com/purchase/command/request/InsertPurchaseRequestHandler.java
package com.purchase.command.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;
import com.purchase.service.request.PurchaseRequestService;
import com.purchase.vo.PurchaseRequest;
import mvc.command.CommandHandler;

public class InsertPurchaseRequestHandler implements CommandHandler {

    private final PurchaseRequestService requestService = new PurchaseRequestService();
    private final ProductService productService = new ProductService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // 진행중(A)만 노출
            Map<String,String> cond = new HashMap<>();
            List<Product> productList = productService.getProductsByConditions(cond, false);
            req.setAttribute("productList", productList);
            return "/WEB-INF/view/purchaseRequestForm.jsp";

        } else if ("POST".equalsIgnoreCase(req.getMethod())) {
            String productId     = nz(req.getParameter("product_id"));
            String quantityStr   = nz(req.getParameter("quantity"));
            String requesterName = nz(req.getParameter("requester_name"));

            // 에러 시 다시 보여줄 데이터 유지 (진행중 A만)
            Map<String,String> cond = new HashMap<>();
            req.setAttribute("productList", productService.getProductsByConditions(cond, false));
            req.setAttribute("selectedProductId", productId);

            // 1) 필수값 검증
            if (productId.isEmpty()) {
                req.setAttribute("formError", "상품을 선택해 주세요.");
                return "/WEB-INF/view/purchaseRequestForm.jsp";
            }
            if (requesterName.isEmpty()) {
                req.setAttribute("formError", "요청자를 입력해 주세요.");
                return "/WEB-INF/view/purchaseRequestForm.jsp";
            }

            // 2) 수량 파싱 (양의 정수)
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity < 1) throw new NumberFormatException("quantity < 1");
            } catch (Exception e) {
                req.setAttribute("formError", "수량은 1 이상의 정수로 입력해 주세요.");
                return "/WEB-INF/view/purchaseRequestForm.jsp";
            }

            // 3) 저장
            try {
                PurchaseRequest pr = new PurchaseRequest();
                // ★ VO가 snake_case 세터를 사용하는 기존 구조에 맞춤
                pr.setProduct_id(productId);
                pr.setQuantity(quantity);
                pr.setRequester_name(requesterName);

                // 날짜/상태는 DB 기본값 권장(SYSDATE 등)
                requestService.addPurchaseRequest(pr);
            } catch (Exception e) {
                req.setAttribute("formError", "등록 중 오류가 발생했습니다: " + e.getMessage());
                return "/WEB-INF/view/purchaseRequestForm.jsp";
            }

            // 4) 성공 시 목록으로
            res.sendRedirect(req.getContextPath() + "/requestList.do");
            return null;

        } else {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }

    private static String nz(String s){ return s == null ? "" : s.trim(); }
}
