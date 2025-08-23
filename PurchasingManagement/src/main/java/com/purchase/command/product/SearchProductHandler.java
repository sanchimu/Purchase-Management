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
		
		//상품 검색에 사용할 4가지 조건 값 변수 지정
		String productId = req.getParameter("product_id");		//상품 id
        String productName = req.getParameter("product_name");	//상품명
        String category = req.getParameter("category");			//카테고리
        String supplierId = req.getParameter("supplier_id");	//공급업체 id

        List<String> categoryList =productService.getCategoryList();  //카테고리 드롭박스화를 위해 카테고리 필드 값들을 String형태 List로 수집
        
        Map<String, String> searchParams = new HashMap<>();		//Map<>이라는 구조가 키와 값을 한번에 저장할 수 있는거라고 함
        //처음에 각 받는 키마다 setAttribute를 썻는데 검색으로 들어온 값을 한번에 묶어서 처리하려면 이게 Map<>을 쓰는게 좋다고해서 써봄
        //setAttribute로 하면 개별적으로 키값을 불러와야하는데 이거 쓰면 그냥 이 searchParams를 불러왔을때 모든 키값이 저장되어있으니 가져오기가 편해서 이게 좋은편이라고 이해
        
        if(productId != null && !productId.trim().isEmpty()) {	//입력 받은 productID의 값이 비어있는지를 체크
            searchParams.put("product_id", productId.trim());	//비어있지 않다면 (!=) 키와 값을 모아두는 searchParams의 공간에 해당 키와 값을 저장 
        }
        if(productName != null && !productName.trim().isEmpty()) { 	//첫번째에 설명 완료 (반복)
            searchParams.put("product_name", productName.trim());
        }
        if(category != null && !category.trim().isEmpty()) {		//첫번째에 설명 완료 (반복)
            searchParams.put("category", category.trim());
        }
        if(supplierId != null && !supplierId.trim().isEmpty()) {	//첫번째에 설명 완료 (반복)
            searchParams.put("supplier_id", supplierId.trim());
        }
        
        
        List<Product> productList = productService.getProductsByConditions(searchParams); // 입력받은 값(searchParams를 ProductService에 있는 getProductsByConditions메서드에 반환
        
        if (productList == null || productList.isEmpty()) {	//검색조건에 만족되는 값이
            // 검색 실패 → 전체 리스트 조회
        	req.setAttribute("NoSearchProductResult", true);
        	productList = productService.getAllProducts();
        }
        
		 productList.sort((p1, p2) -> {												//정렬을 위한 sort메서드인데 리스트 정렬을 위해 자주 사용하는 메서드라고 함, 이게 알아보니 좋은게 while, for 이런거 안써도 알아서 반복해주는 메서드라고함
			    boolean p1Discontinued = "販売終了".equals(p1.getProduct_status());	//p1이 販売終了상태일 경우 (.equals)를 확인 (boolean값이니 販売終了일경우 1, 아닐경우 0
			    boolean p2Discontinued = "販売終了".equals(p2.getProduct_status());	//위와 동일하게 p2의 상테 체크
			    if (p1Discontinued && !p2Discontinued) return 1;					//p1이 販売終了이고 p2가 아닐경우 1을 반환
			    if (!p1Discontinued && p2Discontinued) return -1;					//반대로 p1이 아니고 p2가 販売終了일경우 -1일 반환
			    return 0;
			});																		//여기서 반환되는 1, 0, -1은 p1이 p2의 어디로 가야하는지를 설정하는 값이라고함
        

        // 4. JSP에 결과 전달
        req.setAttribute("productList", productList);	//productList라는 키와 productList 값을 반환
        req.setAttribute("categoryList", categoryList);	//categoryList라는 키와 categoryList 값을 반환
        // 5. 뷰 이름 반환 (productList.jsp 경로)
        
        req.getRequestDispatcher("/WEB-INF/view/productList.jsp").forward(req, res); //원래 req에 저장되어 있던 값들을 유지하며 경로의 jsp를 실행
        //redirect와의 다른점은 req에 값을 유지하느냐 리셋하느냐의 느낌이 큰것으로 보임
        return null;
    }	
		// TODO Auto-generated method stub
}


