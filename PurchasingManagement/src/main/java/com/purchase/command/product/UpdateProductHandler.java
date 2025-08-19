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
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
        // 상품 목록을 Service를 통해 가져오기
        List<Product> productList = productService.getAllProducts();

        if (productList != null) {
            for (int i = 0; i < productList.size(); i++) { // 클래식 for문
                Product p = productList.get(i);

                String productStatus = req.getParameter(p.getProduct_id());

                if (productStatus != null) {
                    Product product = new Product();
                    product.setProduct_id(p.getProduct_id());
                    product.setProduct_status(productStatus);

                    productService.updateProductStatus(product);
                }
            }
        }

	        // 완료 후 다시 목록 페이지로 이동
        res.sendRedirect(req.getContextPath() + "/listProducts.do");
        return null;
	}

}
