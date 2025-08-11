package com.purchase.command.product;

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
		 List<String> categoryList =productService.getCategoryList(); 
        
        req.setAttribute("productList", productList);
		 req.setAttribute("categoryList", categoryList); 
		 
        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/view/productList.jsp");
        rd.forward(req, res);
        
		/* return "/WEB-INF/view/productList.jsp"; */
        return null;
	}
}
