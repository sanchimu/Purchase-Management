package com.purchase.command.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;

import mvc.command.CommandHandler;

public class ModifyProductHandler implements CommandHandler {

	ProductService productService = new ProductService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		if(req.getMethod().equalsIgnoreCase("GET")) {
			
			String productId = req.getParameter("product_id");
	        Product product = productService.getProductById(productId);

	        // JSP에서 product 꺼낼 수 있도록 저장
	        req.setAttribute("product", product);
			
			return "WEB-INF/view/productModify.jsp";
		}else if(req.getMethod().equalsIgnoreCase("POST")) {
			 Product product = new Product();
		        product.setProduct_id(req.getParameter("product_id"));
		        product.setProduct_name(req.getParameter("product_name"));
		        product.setCategory(req.getParameter("category"));
		        product.setPrice(Integer.parseInt(req.getParameter("price")));
		        product.setSupplier_id(req.getParameter("supplier_id"));
		        product.setProduct_status(req.getParameter("product_status"));

		        
		        productService.modifyProduct(product); // 전체 업데이트용 메서드 필요

		        res.sendRedirect("listProducts.do");
		        return null;
		}
		
		return null;
	}

}
