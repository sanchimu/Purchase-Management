package com.purchase.command.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.product.ProductService;

import mvc.command.CommandHandler;

public class DeleteProductHandler implements CommandHandler { // 삭제 기능 제거로 사용 x 하지만 추후 삭제 기능 원복 가능성을 고려해 남겨둠

	private ProductService productService = new ProductService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
	    String[] productIds = req.getParameterValues("productIds");

        if (productIds != null && productIds.length > 0) {
            productService.deleteProduct(productIds);
            req.setAttribute("deletedCount", productIds.length);
            return "/WEB-INF/view/deleteSuccess.jsp";
        } else {
            req.setAttribute("error", "삭제할 상품을 선택하세요.");
            return "/WEB-INF/view/deleteFail.jsp";
        }
    }
}
