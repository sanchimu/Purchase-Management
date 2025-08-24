package com.purchase.command.product;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.product.ProductService;
import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.Product;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

public class AddProductHandler implements CommandHandler {

	private ProductService productService = new ProductService(); //ProductService에 있는 메서드 사용을 위해 productSerivce라는 객체를 생성한다.
	// ProductServiceにあるメソッドを使用するため、productServiceというオブジェクトを作成する。
	private SupplierInfoService supplierInfoService = new SupplierInfoService(); //ProductService에 있는 메서드 사용을 위해 productSerivce라는 객체를 생성한다.
	// SupplierInfoServiceにあるメソッドを使用するため、supplierInfoServiceというオブジェクトを作成する。
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
	    
		if(req.getMethod().equalsIgnoreCase("GET")) {
			List<SupplierInfo> supplierList =supplierInfoService.getSupplierIdNameList(); 
			// String 형태의 배열로 supplierList라는 List객체 생성 후 productService의 getSupplierList 메서드의 return값을 부여받는다.
			// String型のリストとしてsupplierListというListオブジェクトを作成し、supplierInfoServiceのgetSupplierIdNameListメソッドの戻り値を代入する。
			
			req.setAttribute("supplierList", supplierList);  // req: 서버가 받는 값, 즉 서버가 parameter값을 받아 세팅(set이니까)한다. supplierList라는 키에(첫번째 인자값) supplierList의 정보를(두번째 인자)
			// req: サーバーが受け取る値、すなわちサーバーがparameter値を受け取り設定(set)する。supplierListというキーに(第1引数) supplierListの情報を(第2引数)
			
			return "/WEB-INF/view/AddProduct.jsp"; // 이후 AddProduct.jsp 창을 다시 띄워준다.
			// その後、AddProduct.jsp画面を再表示する。
			
		} else if(req.getMethod().equalsIgnoreCase("POST")) {
		String priceStr = req.getParameter("price"); // parameter 값을 받을땐 모두 String값으로 들어가기 때문에 int 값이 들어가는 price의 경우 우선 String 클래스로 값을 저장한다. (후에 int로 형변환 진행 - 서버로 값 올릴땐 String 클라이언에트에서 사용할땐 형변환으로 int) 41행
		// parameter値はすべてString型で入るため、int型の値が入るpriceの場合は一旦Stringで受け取り、後でint型に変換する。 (41行目)
		
		//상품 생성 시 유저가 기입할 정보들을 서버로 보내줄 객체 생성
		// 商品作成時にユーザーが入力した情報をサーバーに送るためのオブジェクト作成
		String productName = req.getParameter("product_name"); 		//상품 이름 (商品名)
		String category = req.getParameter("category");				//카테고리 (カテゴリ)
		String supplierId = req.getParameter("supplier_id");		//공급 업체 아이디(仕入先ID)
		String productStatus = req.getParameter("product_status");	//상품 상태 (商品状態)
		
	

		//고민인 부분 : 
		
	    if (productName == null || productName.trim().isEmpty()) { //값([productName이 없는 경우를 뜻하는데 ||(or)을 이용해 두가지 경우를 체크 / null은 값이 없을 때이고 혹시 공백만 들어가있을 경우는 null이 아니기 때문에 trim(공백 제거 처리)후 isEmpty를 써서 공백을 제거 한뒤 값이 없는지 체크
	    	// productNameがない場合、または空白のみの場合をチェック
	    	
	        // price 값이 없으면 입력 폼 보여주기 (초기 진입 시)
	    	// priceがない場合、入力フォームを再表示する（初回アクセス時）
	    	req.setAttribute("noProductNameError", "商品名がありません。");//상품 이름이 없을 경우 다시 창을 띄워주기전 error메시지를 띄우기 위해 가격이 없을 때 뜨는 에러 용 키값인 noPriceError에 가격없음 문구를 넣어준다.
	    	// 商品名がない場合、再度画面を表示する前にエラーメッセージをセットするため、noProductNameErrorキーにメッセージを設定
	    	res.sendRedirect(req.getContextPath() + "/listProducts.do");
	    	return null; // 이후 AddProduct.jsp 창을 다시 띄워준다. 
	    	// その後、AddProduct.jsp画面を再表示する。
	        
	   	}
	    
	    if (category == null || category.trim().isEmpty()) { //값([category가 없는 경우를 뜻하는데 ||(or)을 이용해 두가지 경우를 체크 / null은 값이 없을 때이고 혹시 공백만 들어가있을 경우는 null이 아니기 때문에 trim(공백 제거 처리)후 isEmpty를 써서 공백을 제거 한뒤 값이 없는지 체크
	    	// categoryがない場合、または空白のみの場合をチェック
	    	
	    	req.setAttribute("noCategoryError", "分類がありません。");//카테고리가 없을 경우 다시 창을 띄워주기전 error메시지를 띄우기 위해 가격이 없을 때 뜨는 에러 용 키값인 noPriceError에 가격없음 문구를 넣어준다.
	    	// カテゴリがない場合、再度画面を表示する前にエラーメッセージをセットするため、noCategoryErrorキーにメッセージを設定
	    	res.sendRedirect(req.getContextPath() + "/listProducts.do");
	    	return null; // 이후 AddProduct.jsp 창을 다시 띄워준다.  
	    	// その後、AddProduct.jsp画面を再表示する。
	    }
	    
		if (priceStr == null || priceStr.trim().isEmpty()) { //값([price가 없는 경우를 뜻하는데 ||(or)을 이용해 두가지 경우를 체크 / null은 값이 없을 때이고 혹시 공백만 들어가있을 경우는 null이 아니기 때문에 trim(공백 제거 처리)후 isEmpty를 써서 공백을 제거 한뒤 값이 없는지 체크

	    	req.setAttribute("noPriceError", "価格がありません。");//가격이 없을 경우 다시 창을 띄워주기전 error메시지를 띄우기 위해 가격이 없을 때 뜨는 에러 용 키값인 noPriceError에 가격없음 문구를 넣어준다.
	    	// 価格がない場合、再度画面を表示する前にエラーメッセージをセットするため、noPriceErrorキーにメッセージを設定
	    	res.sendRedirect(req.getContextPath() + "/listProducts.do");
	    	return null; // 이후 AddProduct.jsp 창을 다시 띄워준다. 
	    	// その後、AddProduct.jsp画面を再表示する。
	    }
	    
	    if (supplierId == null || supplierId.trim().isEmpty()) { //값([supplierId가 없는 경우를 뜻하는데 ||(or)을 이용해 두가지 경우를 체크 / null은 값이 없을 때이고 혹시 공백만 들어가있을 경우는 null이 아니기 때문에 trim(공백 제거 처리)후 isEmpty를 써서 공백을 제거 한뒤 값이 없는지 체크

	    	req.setAttribute("noSupplierIdError", "仕入先IDがありません。");//가격이 없을 경우 다시 창을 띄워주기전 error메시지를 띄우기 위해 가격이 없을 때 뜨는 에러 용 키값인 noPriceError에 가격없음 문구를 넣어준다.
	    	// 仕入先IDがない場合、再度画面を表示する前にエラーメッセージをセットするため、noSupplierIdErrorキーにメッセージを設定
	    	res.sendRedirect(req.getContextPath() + "/listProducts.do");
	    	return null; // 이후 AddProduct.jsp 창을 다시 띄워준다. 
	    	// その後、AddProduct.jsp画面を再表示する。
	    }
		
		int price = Integer.parseInt(priceStr); // 서버에 String형태로 보냈던 price값을 int형으로 사용하기 위해 형변환을 진행한다.
		// サーバーにString型で送られてきたprice値をint型に変換する
		
		Product product = new Product(null, productName, category, price, supplierId, productStatus); //product 객체 생성 후
		// productオブジェクトを作成
		productService.addProduct(product); //prodcutService의 addProduct 메서드의 매개변수로 product를 대입한다.
		// productServiceのaddProductメソッドにproductを渡す
	  
	    res.sendRedirect(req.getContextPath() + "/listProducts.do?success=true"); 
	    //원래 setAttribute로 바로 윗줄 success를 보내 Add 성공 시 success 키값으로 성공 팝업을 띄우려 했지만 단순 return이 아닌 redirect형식으로는 저 키값을 보내도 바로 /listProducts.do가 실행되서 키값이 들어가도 redirect때문에 바로 null로 바뀌어서 팝업창이 생성 안된다고 함
	    // 元はsetAttributeでsuccessキーを送信してAdd成功時にポップアップを表示しようとしていたが、redirect形式ではキーを送っても/listProducts.doがすぐに実行されるため、値はnullになりポップアップが表示されない
	    //때문에 listProducts.do 뒤에 success=ture를 넣어 productList로 이동 후 사용하기 위해 ?success=ture코드를 넣음
	 // そのためlistProducts.doの後ろに?success=trueを付与してproductListに遷移させる
	    return null;
	}
	return null;
	}
}
