package com.purchase.command.request;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.request.PurchaseRequestService;
import com.purchase.vo.PurchaseRequest;

import mvc.command.CommandHandler;

public class InsertPurchaseRequestHandler implements CommandHandler {

	private final PurchaseRequestService service = new PurchaseRequestService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		 // 요청 메서드가 GET이면 → 등록 폼 화면 표시(リクエストメソッドがGETの場合 → 登録フォームを表示)
		if (req.getMethod().equalsIgnoreCase("GET")) {
			return "/WEB-INF/view/purchaseRequestForm.jsp";
			
	        // 요청 메서드가 POST면 → 등록 처리 실행(リクエストメソッドがPOSTの場合 → 登録処理を実行)
		} else if (req.getMethod().equalsIgnoreCase("POST")) {

			String productId = req.getParameter("product_id");
			String quantityStr = req.getParameter("quantity");
			String requesterName = req.getParameter("requester_name");

			int quantity = Integer.parseInt(quantityStr);

			PurchaseRequest request = new PurchaseRequest(
					null, // request_id는 null로 두고 DAO에서 설정(request_idは nullにしてDAO側で設定）
					productId, 
					quantity, 
					new Date(), 
					requesterName);

			service.addPurchaseRequest(request);

			// 등록 완료 후 목록 페이지로 리다이렉트(登録完了後、一覧ページへリダイレクト)
			res.sendRedirect(req.getContextPath() + "/requestList.do");
			return null;

		} else {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}
}
