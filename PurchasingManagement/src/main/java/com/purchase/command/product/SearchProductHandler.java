package com.purchase.command.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;

import mvc.command.CommandHandler;

public class SearchProductHandler implements CommandHandler {
	private ProductService productService = new ProductService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String productId = req.getParameter("product_id");
        String productName = req.getParameter("product_name");
        String category = req.getParameter("category");
        String supplierId = req.getParameter("supplier_id");

        List<String> categoryList =productService.getCategoryList(); 
        // 2. 조건 Map에 넣기 (널/빈문자 체크)
        Map<String, String> searchParams = new HashMap<>();
        if(productId != null && !productId.trim().isEmpty()) {
            searchParams.put("product_id", productId.trim());
        }
        if(productName != null && !productName.trim().isEmpty()) { 
            searchParams.put("product_name", productName.trim());
        }
        if(category != null && !category.trim().isEmpty()) {
            searchParams.put("category", category.trim());
        }
        if(supplierId != null && !supplierId.trim().isEmpty()) {
            searchParams.put("supplier_id", supplierId.trim());
        }

        // 3. 서비스 호출 → 조건에 맞는 상품 리스트 조회
        List<Product> productList = productService.getProductsByConditions(searchParams);

        // 4. JSP에 결과 전달
        req.setAttribute("productList", productList);
        req.setAttribute("categoryList", categoryList);
        // 5. 뷰 이름 반환 (productList.jsp 경로)
        return "/WEB-INF/view/productList.jsp";
    }
		// TODO Auto-generated method stub
}


