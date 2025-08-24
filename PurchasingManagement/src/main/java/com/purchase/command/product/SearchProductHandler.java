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
		// 商品検索に使用する4つの条件変数を指定
		String productId = req.getParameter("product_id");		//상품 id (商品ID)
        String productName = req.getParameter("product_name");	//상품명 (商品名)
        String category = req.getParameter("category");			//카테고리 (カテゴリ)
        String supplierId = req.getParameter("supplier_id");	//공급업체 id (仕入先ID)

        List<String> categoryList =productService.getCategoryList();  	//카테고리 드롭박스화를 위해 카테고리 필드 값들을 String형태 List로 수집
        																// カテゴリのドロップダウン用にカテゴリフィールド値をString型リストとして取得
        
        Map<String, String> searchParams = new HashMap<>();		//Map<>이라는 구조가 키와 값을 한번에 저장할 수 있는거라고 함
        														// Map<>という構造はキーと値を同時に保存できる	
        
        //처음에 각 받는 키마다 setAttribute를 썻는데 검색으로 들어온 값을 한번에 묶어서 처리하려면 이게 Map<>을 쓰는게 좋다고해서 써봄
        //setAttribute로 하면 개별적으로 키값을 불러와야하는데 이거 쓰면 그냥 이 searchParams를 불러왔을때 모든 키값이 저장되어있으니 가져오기가 편해서 이게 좋은편이라고 이해
        
        if(productId != null && !productId.trim().isEmpty()) {	//입력 받은 productID의 값이 비어있는지를 체크
        														// 入力されたproductIDの値が空かどうかをチェック
            searchParams.put("product_id", productId.trim());	//비어있지 않다면 (!=) 키와 값을 모아두는 searchParams의 공간에 해당 키와 값을 저장
            													// 空でなければ、キーと値をsearchParamsに保存
        }															
        if(productName != null && !productName.trim().isEmpty()) { 	//첫번째에 설명 완료 (반복) - （繰り返し）
            searchParams.put("product_name", productName.trim());
        }
        if(category != null && !category.trim().isEmpty()) {		//첫번째에 설명 완료 (반복) - （繰り返し）
            searchParams.put("category", category.trim());
        }
        if(supplierId != null && !supplierId.trim().isEmpty()) {	//첫번째에 설명 완료 (반복) - （繰り返し）
            searchParams.put("supplier_id", supplierId.trim());
        }
        
        
        List<Product> productList = productService.getProductsByConditions(searchParams); 	// 입력받은 값(searchParams를 ProductService에 있는 getProductsByConditions메서드에 반환
        																					// 入力された値(searchParams)をProductServiceのgetProductsByConditionsメソッドに渡して取得
        if (productList == null || productList.isEmpty()) {	//검색조건에 만족되는 값이 없으면 에러 빌셍영 키값 갱신 후 상품 정보 대입
        													//色調件に満足する値がなければ、エラービルセンヤングキー値更新後、商品情報を代入
        	req.setAttribute("NoSearchProductResult", true);
        	productList = productService.getAllProducts();
        }
        
		 productList.sort((p1, p2) -> {	//정렬을 위한 sort메서드인데 리스트 정렬을 위해 자주 사용하는 메서드라고 함, 이게 알아보니 좋은게 while, for 이런거 안써도 알아서 반복해주는 메서드라고함
			 							// ソート用のsortメソッドで、リストを並び替える際によく使われるメソッド。whileやforを使わなくても自動で繰り返してくれるメソッド
			 boolean p1Discontinued = "販売終了".equals(p1.getProduct_status());	//p1이 販売終了상태일 경우 (.equals)를 확인 (boolean값이니 販売終了일경우 1, 아닐경우 0
			    																	// p1が販売終了状態かどうかをチェック (.equals) (boolean値なので販売終了なら1、そうでない場合は0)
			    boolean p2Discontinued = "販売終了".equals(p2.getProduct_status());	//위와 동일하게 p2의 상테 체크
			    																	// 同様にp2の状態をチェック
			    if (p1Discontinued && !p2Discontinued) return 1;					//p1이 販売終了이고 p2가 아닐경우 1을 반환
			    																	// p1が販売終了でp2がそうでない場合、1を返す
			    if (!p1Discontinued && p2Discontinued) return -1;					//반대로 p1이 아니고 p2가 販売終了일경우 -1일 반환
			    																	// 逆にp1がそうでなくp2が販売終了の場合、-1を返す
			    return 0;
			});																		//여기서 반환되는 1, 0, -1은 p1이 p2의 어디로 가야하는지를 설정하는 값이라고함
		 																			// ここで返される1,0,-1はp1がp2のどこに移動すべきかを決める値


        req.setAttribute("productList", productList);	//productList라는 키와 productList 값을 반환
        												// productListというキーとproductList値を渡す
        req.setAttribute("categoryList", categoryList);	//categoryList라는 키와 categoryList 값을 반환
        												// categoryListというキーとcategoryList値を渡す				

        
        req.getRequestDispatcher("/WEB-INF/view/productList.jsp").forward(req, res); 	//원래 req에 저장되어 있던 값들을 유지하며 경로의 jsp를 실행
        																				// 元のreqに保存されている値を保持しつつ、指定のjspを実行
        																				//redirect와의 다른점은 req에 값을 유지하느냐 리셋하느냐의 느낌이 큰것으로 보임
        return null;
    }	
		// TODO Auto-generated method stub
}


