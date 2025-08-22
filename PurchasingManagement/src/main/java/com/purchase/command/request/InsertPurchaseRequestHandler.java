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
        
    	// GET 요청: 폼 화면 표시
    	// GETリクエスト：フォーム画面表示
    	if ("GET".equalsIgnoreCase(req.getMethod())) {
            Map<String,String> cond = new HashMap<>();
            
            // 진행중(A)만 필터링하여 상품 목록 조회(false=종료 제외) 
            // 進行中(A)のみフィルタして商品リストの照会（false=終了除外）
            List<Product> productList = productService.getProductsByConditions(cond, false);
            req.setAttribute("productList", productList);
            return "/WEB-INF/view/purchaseRequestForm.jsp";

         // POST 요청: 폼 제출 처리
         // POSTリクエスト：フォーム送信
        } else if ("POST".equalsIgnoreCase(req.getMethod())) {
            String productId     = nz(req.getParameter("product_id"));
            String quantityStr   = nz(req.getParameter("quantity"));
            String requesterName = nz(req.getParameter("requester_name"));

            // 에러 시 다시 보여줄 데이터 유지 (진행중 A만)
            // エラー時、再度見せるデータ維持(進行中Aのみ)
            Map<String,String> cond = new HashMap<>();
            req.setAttribute("productList", productService.getProductsByConditions(cond, false));
            req.setAttribute("selectedProductId", productId);

            // 상품ID 미입력 체크 
            // 商品ID未入力のチェック
            if (productId.isEmpty()) {
                req.setAttribute("formError", "상품을 선택해 주세요.");
                return "/WEB-INF/view/purchaseRequestForm.jsp";
            }
            
            // 요청자명 미입력 체크 
            // 要請者名未入力のチェック
            if (requesterName.isEmpty()) {
                req.setAttribute("formError", "요청자를 입력해 주세요.");
                return "/WEB-INF/view/purchaseRequestForm.jsp";
            }
            
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                
                // 1 미만이면 오류로 처리 
                // 1未満はエラー扱い
                if (quantity < 1) throw new NumberFormatException("quantity < 1");
            } catch (Exception e) {
                req.setAttribute("formError", "수량은 1 이상의 정수로 입력해 주세요.");
                return "/WEB-INF/view/purchaseRequestForm.jsp";
            }

            try {
                PurchaseRequest pr = new PurchaseRequest();
                
                pr.setProduct_id(productId);
                pr.setQuantity(quantity);
                pr.setRequester_name(requesterName);

                requestService.addPurchaseRequest(pr);
                
             // 에러 발생 시 메시지로 표시 후 폼으로 이동 
             // エラー時メッセージで表示してフォームに移動
            } catch (Exception e) {
                req.setAttribute("formError", "등록 중 오류가 발생했습니다: " + e.getMessage());
                return "/WEB-INF/view/purchaseRequestForm.jsp";
            }

            // 성공 시 목록으로 이동
            // 成功したらリストに移動
            res.sendRedirect(req.getContextPath() + "/requestList.do");
            return null;

        } else {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }

    private static String nz(String s){ return s == null ? "" : s.trim(); }
}
