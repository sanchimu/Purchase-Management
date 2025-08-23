package com.purchase.command.product;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;

import mvc.command.CommandHandler;

public class UpdateProductHandler implements CommandHandler {

		private ProductService productService = new ProductService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {// 상품 상태를 저장하기 위한 핸들러
		// TODO Auto-generated method stub
        
        List<Product> productList = productService.getAllProducts(); //상품 정보를 받기 받기 위한 productList 생성

        if (productList != null) { // 상품정보가 하나라도 있을 시 진행
            for (int i = 0; i < productList.size(); i++) { //productList에 있는 상품 개수만큼 for문 진행
                Product p = productList.get(i); // 상품 DB를 가져옴

                String productStatus = req.getParameter(p.getProduct_id()); //이후 productStatus에 가져온 상품 DB의 상품 ID값을 부여

                if (productStatus != null) { // 상품 상태가 없을 경우
                    Product product = new Product();			//상품 객체 생성
                    product.setProduct_id(p.getProduct_id());	//객체의 product_id를 가져온 값의 값으로 적용
                    product.setProduct_status(productStatus);	//객체의 product_status도 동일하게 적용

                    productService.updateProductStatus(product);	//ProductService의 updateProductStatus 메서드에 product의 값을 부여
                }
            }
        }

	        // 완료 후 다시 목록 페이지로 이동
        res.sendRedirect(req.getContextPath() + "/listProducts.do");	//redirect로 초기화 후 listProduct.do 실행
        return null;
	}

}
