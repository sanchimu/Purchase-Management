package com.purchase.command.product;

import java.util.List;

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
		if(req.getMethod().equalsIgnoreCase("GET")) { // 수정 창에서 기존 정보를 보여주기 위한 GET 방식
			
			String productId = req.getParameter("product_id");
	        Product product = productService.getProductById(productId);
	        List<String> supplierList =productService.getSupplierList(); //ProductService에서 getSupplierList메서드로 생성된 공급업체 리스트를 supplierList에 String 형식으로 저장

	        
	        req.setAttribute("product", product);			//상품 ID값으로 가져온 product 정보를 담을 키값 생성
	        req.setAttribute("supplierList", supplierList); //위에서 가져왔던 공급업체 리스트를 supplierList 키에 저장
	        
			return "WEB-INF/view/productModify.jsp";
		}else if(req.getMethod().equalsIgnoreCase("POST")) { 	// 수정한 정보를 갱신학 위한 POST 방식
			 Product product = new Product();					//입력받은 값을 저장하기 위한 product 객체 생성
		        product.setProduct_id(req.getParameter("product_id"));
		        product.setProduct_name(req.getParameter("product_name"));
		        product.setCategory(req.getParameter("category"));
		        product.setPrice(Integer.parseInt(req.getParameter("price")));
		        product.setSupplier_id(req.getParameter("supplier_id"));
		        product.setSupplier_name(req.getParameter("supplier_name"));
		        product.setProduct_status(req.getParameter("product_status"));
		        //값 부여
		        
		        productService.modifyProduct(product); 	// 전체 업데이트용 메서드 필요

		        res.sendRedirect("listProducts.do");	//수정 완료 후 다시 리스트 페이지로 이동
		        return null;
		}
		
		return null;
	}

}
