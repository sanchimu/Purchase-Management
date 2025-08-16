// com.purchase.command.request.InsertPurchaseRequestHandler
package com.purchase.command.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;
import com.purchase.service.request.PurchaseRequestService;
import mvc.command.CommandHandler;

public class InsertPurchaseRequestHandler implements CommandHandler {

	private final PurchaseRequestService requestService = new PurchaseRequestService();
	private final ProductService productService = new ProductService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (req.getMethod().equalsIgnoreCase("GET")) {
			// ★ 상품 목록 조회하여 폼에 전달
			List<Product> productList = productService.getAllProducts();
			req.setAttribute("productList", productList);

			// 목록이 비었다면 안내 메시지
			if (productList == null || productList.isEmpty()) {
				req.setAttribute("noProductMsg", "등록된 상품이 없습니다. 먼저 상품을 등록해 주세요.");
			}
			return "/WEB-INF/view/purchaseRequestForm.jsp";

		} else if (req.getMethod().equalsIgnoreCase("POST")) {
			// 기존 로직 그대로, 단 product_id는 select에서 넘어오는 값을 사용
			String productId = req.getParameter("product_id"); // <-- select name과 일치
			String quantityStr = req.getParameter("quantity");
			String requesterName = req.getParameter("requester_name");

			// 기본 검증
			if (productId == null || productId.isEmpty()) {
				req.setAttribute("formError", "상품을 선택해 주세요.");
				// 에러 시에도 다시 상품목록을 넣어줘야 화면에 보임
				req.setAttribute("productList", productService.getAllProducts());
				return "/WEB-INF/view/purchaseRequestForm.jsp";
			}

			// 나머지 생성/저장 로직은 기존과 동일하게…
			// requestService.addPurchaseRequest(new PurchaseRequest(...));

			// 성공 시 목록으로 리다이렉트 등
			return "redirect:/listpurchaserequest.do";
		} else {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}
}
