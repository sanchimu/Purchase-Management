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
        
        List<Product> productList = productService.getAllProducts(); 	//상품 정보를 받기 받기 위한 productList 생성
        																// 商品情報を取得するためのproductListを作成
        if (productList != null) { 	// 상품정보가 하나라도 있을 시 진행
        							// 商品情報が1件でも存在する場合に処理
            for (int i = 0; i < productList.size(); i++) { 	//productList에 있는 상품 개수만큼 for문 진행
            												// productListにある商品数だけfor文を実行
                Product p = productList.get(i); // 상품 DB를 가져옴
                								// 商品DBを取得

                String productStatus = req.getParameter(p.getProduct_id()); //이후 productStatus에 가져온 상품 DB의 상품 ID값을 부여
                															// 取得した商品DBのproduct ID値をproductStatusに設定
                if (productStatus != null) { 	// 상품 상태가 없을 경우
                								// 商品状態がない場合
                    Product product = new Product();			//상품 객체 생성
                    
                    product.setProduct_id(p.getProduct_id());	// 객체의 product_id를 가져온 값의 값으로 적용
                    											// 商品オブジェクトを作成
                    product.setProduct_status(productStatus);	// 객체의 product_status도 동일하게 적용
                    											// オブジェクトのproduct_idに取得値を設定
                    productService.updateProductStatus(product);	// ProductService의 updateProductStatus 메서드에 product의 값을 부여
                    												// ProductServiceのupdateProductStatusメソッドにproductの値を渡す
                }
            }
        }

	        // 완료 후 다시 목록 페이지로 이동
        res.sendRedirect(req.getContextPath() + "/listProducts.do");	//redirect로 초기화 후 listProduct.do 실행
        																// redirectで初期化後、listProducts.doを実行
        return null;
	}

}
