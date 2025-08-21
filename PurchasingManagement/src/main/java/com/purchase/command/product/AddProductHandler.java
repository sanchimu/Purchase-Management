package com.purchase.command.product;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;

import mvc.command.CommandHandler;

public class AddProductHandler implements CommandHandler {

	private ProductService productService = new ProductService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
	    
		
		List<String> supplierList =productService.getSupplierList(); 
		//System.out.println("Handler - supplierList: " + supplierList);
		req.setAttribute("supplierList", supplierList); 
		
		String priceStr = req.getParameter("price");
		
	    if (priceStr == null || priceStr.trim().isEmpty()) {
	        // price 값이 없으면 입력 폼 보여주기 (초기 진입 시)
	        return "/WEB-INF/view/AddProduct.jsp";
	    }
		
		String productName = req.getParameter("product_name");
		String category = req.getParameter("category");
		String supplierId = req.getParameter("supplier_id");
		String productStatus = req.getParameter("product_status");
		
		
		
		
		if(priceStr == null || priceStr.trim().isEmpty()) {
		    // 예외 처리 또는 기본값 지정
		    throw new IllegalArgumentException("가격 정보가 없습니다.");
		}
		
		int price = Integer.parseInt(priceStr);
		
		Product product = new Product(null, productName, category, price, supplierId, productStatus);
		productService.addProduct(product);
		
	    req.setAttribute("success", true);
	    
	    // 다시 addProduct.jsp 보여주기
	    res.sendRedirect(req.getContextPath() + "/listProducts.do");
	    return null;
	}
}
