package com.purchase.command.product;

import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.dao.product.ProductDao;
import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;

import mvc.command.CommandHandler;

public class ListProductHandler implements CommandHandler {

	private ProductService productService = new ProductService();
	private ProductDao productDao = new ProductDao();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		
		
		 List<Product> productList = productService.getAllProducts();
		 productList.sort((p1, p2) -> {
			    boolean p1Discontinued = "단종".equals(p1.getProduct_status());
			    boolean p2Discontinued = "단종".equals(p2.getProduct_status());
			    if (p1Discontinued && !p2Discontinued) return 1;
			    if (!p1Discontinued && p2Discontinued) return -1;
			    return 0;
			});
		 
		 
		 
		 List<String> categoryList =productService.getCategoryList(); 
		 
		 
		 List<String> productStatusList = Arrays.asList("판매중", "판매중지", "품절", "보류");
         
         req.setAttribute("productList", productList);
		 req.setAttribute("categoryList", categoryList); 
		 
		 
		 req.setAttribute("productStatusList", productStatusList);
		 
         RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/view/productList.jsp");
         rd.forward(req, res);
        
		 /* return "/WEB-INF/view/productList.jsp"; */
         return null;
	}
}
