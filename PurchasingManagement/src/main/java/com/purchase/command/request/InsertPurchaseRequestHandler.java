// com.purchase.command.request.InsertPurchaseRequestHandler
package com.purchase.command.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;
import com.purchase.service.request.PurchaseRequestService;
import com.purchase.vo.PurchaseRequest; // ★ 필요
import mvc.command.CommandHandler;

public class InsertPurchaseRequestHandler implements CommandHandler {

	private final PurchaseRequestService requestService = new PurchaseRequestService();
	private final ProductService productService = new ProductService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (req.getMethod().equalsIgnoreCase("GET")) {
			// 상품 목록 조회하여 폼에 전달
			List<Product> productList = productService.getAllProducts();
			req.setAttribute("productList", productList);

			// JSP는 empty productList로 분기하므로 noProductMsg는 불필요
			return "/WEB-INF/view/purchaseRequestForm.jsp";

		} else if (req.getMethod().equalsIgnoreCase("POST")) {
			String productId = req.getParameter("product_id");
			String quantityStr = req.getParameter("quantity");
			String requesterName = req.getParameter("requester_name");

			// 에러 시에도 다시 보여줄 데이터
			req.setAttribute("productList", productService.getAllProducts());
			req.setAttribute("selectedProductId", productId); // ★ 선택값 유지

			// 1) 필수값 검증
			if (productId == null || productId.isEmpty()) {
				req.setAttribute("formError", "상품을 선택해 주세요.");
				return "/WEB-INF/view/purchaseRequestForm.jsp";
			}
			if (requesterName == null || requesterName.isEmpty()) {
				req.setAttribute("formError", "요청자를 입력해 주세요.");
				return "/WEB-INF/view/purchaseRequestForm.jsp";
			}

			// 2) 수량 안전 파싱
			int quantity;
			try {
				quantity = Integer.parseInt(quantityStr);
				if (quantity < 1)
					throw new NumberFormatException("quantity < 1");
			} catch (Exception e) {
				req.setAttribute("formError", "수량은 1 이상의 정수로 입력해 주세요.");
				return "/WEB-INF/view/purchaseRequestForm.jsp";
			}

			// 3) 저장
			try {
				PurchaseRequest pr = new PurchaseRequest();
				pr.setProduct_id(productId);
				pr.setQuantity(quantity);
				pr.setRequester_name(requesterName);
				// pr.setRequest_date(...) 는 DB 기본값(SYSDATE) 권장
				// pr.setRequest_status("PENDING"); // 필요 시
				requestService.addPurchaseRequest(pr);
			} catch (Exception e) {
				// DAO에서 ORA-에러(중복키/ FK 등) 발생 시 메시지 처리
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
}