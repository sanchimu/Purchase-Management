package com.purchase.command.product;

import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;

import mvc.command.CommandHandler;

public class ListProductHandler implements CommandHandler { // 상품 리스트를 띄워주기 위한 핸들러 파일
															// 商品リストを表示するためのハンドラーファイル
	private ProductService productService = new ProductService(); 	//ProductService에 있는 메서드 사용을 위해 productSerivce라는 객체를 생성한다.
																	// ProductServiceにあるメソッドを使用するため、productServiceというオブジェクトを作成する。
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		
		
		 List<Product> productList = productService.getAllProducts();
		 productList.sort((p1, p2) -> {	//정렬을 위한 sort메서드인데 리스트 정렬을 위해 자주 사용하는 메서드라고 함, 이게 알아보니 좋은게 while, for 이런거 안써도 알아서 반복해주는 메서드라고함
			 							// ソート用のsortメソッドで、リストを並び替える際によく使われるメソッド。whileやforを使わなくても自動で繰り返してくれるメソッド
			    boolean p1Discontinued = "販売終了".equals(p1.getProduct_status());	//p1이 販売終了상태일 경우 (.equals)를 확인 (boolean값이니 販売終了일경우 1, 아닐경우 0
			    																	// p1が販売終了状態かをチェックする (.equalsを使用) (boolean値なので、販売終了の場合は1、そうでない場合は0)
			    boolean p2Discontinued = "販売終了".equals(p2.getProduct_status());	//위와 동일하게 p2의 상테 체크
			    																	// 同様にp2の状態をチェック
			    if (p1Discontinued && !p2Discontinued) return 1;					//p1이 販売終了이고 p2가 아닐경우 1을 반환
			    																	// p1が販売終了でp2がそうでない場合、1を返す
			    if (!p1Discontinued && p2Discontinued) return -1;					//반대로 p1이 아니고 p2가 販売終了일경우 -1일 반환
			    																	// 逆にp1がそうでなくp2が販売終了の場合、-1を返す
			    return 0;
			});																		//여기서 반환되는 1, 0, -1은 p1이 p2의 어디로 가야하는지를 설정하는 값이라고함
		 																			// ここで返される1,0,-1はp1がp2のどこに移動すべきかを決める値

         req.setAttribute("productList", productList);	//getAllProduct에서 생성한 product의 리스트를 productList라는 키값에 부여
         												// getAllProductsで取得したproductリストをproductListというキーにセット

         req.getRequestDispatcher("/WEB-INF/view/productList.jsp").forward(req, res); 	//원래 req에 저장되어 있던 값들을 유지하며 경로의 jsp를 실행
         																				// 元のreqに保存されている値を保持しつつ、指定のjspを実行
         																				//redirect와의 다른점은 req에 값을 유지하느냐 리셋하느냐의 느낌이 큰것으로 보임
         																				// redirectとの違いはreqの値を保持するかリセットするかの違いが大きい
        
         return null;
	}
}
