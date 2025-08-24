package com.purchase.dao.product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.purchase.vo.Product;

import jdbc.JdbcUtil;

public class ProductDao {
	public Product insert(Connection conn, Product product) throws SQLException {
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			int seqNum = 0;
			pstmt = conn.prepareStatement("select product_seq.nextval from dual"); 	//시퀀스 값을 가져오기위해한건데 쿼리에는 무조건 from이 필요해서 dual이라는 오라클에서 제공하는 가상 테이블을 활용
																					//シーケンス値を取得するため。クエリには必ずFROMが必要なので、Oracleが提供する仮想テーブルdualを使用
			rs = pstmt.executeQuery(); //DB값(시퀀스 값) 받음 (DBから値（シーケンス値）を取得)
			if (rs.next()) {//시퀀스가 있을 경우 seqNum이 0 -> 1로 바뀜
							//シーケンスがある場合seqNumが0->1に変更
				seqNum = rs.getInt(1);
			}
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt); 	//ResultSet과 PreparedStatement를 사용했으니 close 처리로 마감 (안해도 기능 문제는 없지만 메모리 누수 등의 손해 발생)
									//ResultSetとPreparedStatementを使ったので、close処理で終了（やらなくても機能には問題ないが、メモリリークなどの損失が発生）
			
			String productId = String.format("P%03d", seqNum);	//특정 문자의 시퀀스 만들기: "P" + 3자리 숫자 (ex. P001)
																//特定文字のシーケンス作成: "P" + 3桁の数字（例 P001）
			product.setProduct_id(productId);					//생성된 product_id를 세팅 (作成されたproduct_idを設定) 

			pstmt = conn.prepareStatement("insert into product values (?,?,?,?,?,?,?)"); //insert into로 테이블에 DB생성 (INSERT INTOでテーブルにDBを作成)
			pstmt.setString(1, product.getProduct_id());
			pstmt.setString(2, product.getProduct_name());
			pstmt.setString(3, product.getCategory());
			pstmt.setInt(4, product.getPrice());
			pstmt.setString(5, product.getSupplier_id());			
			pstmt.setString(6, product.getProduct_status());
			pstmt.setString(7, "A");
			int insertedCount = pstmt.executeUpdate();	//DB에 영향 받은 개수를 insertedCount에 저장 (DBに影響を受けた行数をinsertedCountに保存)
														//executeUpdate와 executeQuery랑 헷갈리지 않도록 조심해야할것같음
			if (insertedCount > 0) {//영향 받은 행 개수 즉, 생성된 행 개수가 있을 시( >0 )
									//影響を受けた行数、つまり作成された行がある場合 (>0)
				stmt = conn.createStatement();
				rs = stmt.executeQuery(
						"select * from (select product_id from product order by product_id desc) where rownum = 1"); 	//product_id로 product 테이블에서 product_id를 찾는데 내림차순(desc, 오름차순은 asc) 으로 정렬해서 찾음, rownum은 그중 첫번째(1)
																														//product_idでproductテーブルでproduct_idを探すのに降順(desc、昇順はasc)で整列して探す、rownumはその中で一番目(1)
				if (rs.next()) {
					return new Product(product.getProduct_id(), product.getProduct_name(), product.getCategory(),
							product.getPrice(), product.getSupplier_id(), product.getProduct_status());	//여기서 next는 생성이 되었다는 뜻(값이 있다는것이니) 그럼 이제 값을 리턴해줘서 받아옴
																										//ここでnextは作成済みという意味、値を返す
				}
			}
			return null;
		} finally {	//마지막으로 close처리 (最後にclose処理)
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
			JdbcUtil.close(stmt);
			// TODO: handle exception
		}
	}

	public int delete(Connection conn, String pno) throws SQLException {
		try (PreparedStatement pstmt = conn.prepareStatement("delete from product where PRODUCT_ID = ?")) { //ID값을 찾아서 해당 DB를 삭제하는 쿼리인데 삭제 기능 제거 됨
			pstmt.setString(1, pno);
			return pstmt.executeUpdate();
		}
	}

	public List<Product> selectAll(Connection conn) throws SQLException {//모든 DB리스트를받아오기 위한 메서드 (全てのDBリストを取得するためのメソッド)
		String sql = "SELECT p.product_id, p.product_name, p.category, p.price, p.supplier_id, s.supplier_name, p.product_status"
				+ " FROM product p JOIN supplier_info s ON p.supplier_id = s.supplier_id ORDER BY p.product_id";
		List<Product> list = new ArrayList<>();	//모든 DB를 가져오기 위해 List형태로 객체 선언
												//全てのDBを取得するためListオブジェクトを宣言
		
		//아래 try는 최신방식이라고 함 좋은것 같음 본래 우리가하던건
		//ResultSet과 PreparedStatemanet를 null로 선언 후 각각 아래 try문에 있는걸 적어줘서 처리했는데
		//그럴 필요 없이 try에 다 넣어서 쓸수있다함
		//구분은 ; 마지막은 ;없이
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql); 	ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {	//rs로 들어온 DB가 나오지 않을 때 까지 while문 반복
								//rsから値がなくなるまでwhileループ
				Product p = new Product(rs.getString("product_id"), rs.getString("product_name"),
						rs.getString("category"), rs.getInt("price"), rs.getString("supplier_id"), rs.getString("supplier_name"), rs.getString("product_status"));
				list.add(p); 	//반복때 마다 list에 product 객체 추가
								//繰り返すごとにlistにProductオブジェクトを追加
			}
		}
		return list;//저장된 정보들 반환 (保存された情報を返す)
	}

	public List<Product> selectByConditions(Connection conn, Map<String, String> conditions) throws SQLException {
		StringBuilder sql = new StringBuilder("SELECT p.product_id, p.product_name, p.category, p.price, " +
			    "p.supplier_id, s.supplier_name, p.product_status " +
			    "FROM product p JOIN supplier_info s ON p.supplier_id = s.supplier_id " +
			    "WHERE 1=1");
		List<Object> params = new ArrayList<>();//조건으로 나온 값들을 저장할 List 변수 params
												//条件で得られた値を保存するList変数params
		if (conditions.get("product_id") != null) {
			sql.append(" AND UPPER(p.product_id) LIKE UPPER(?)");	//상황에 따라 조건을 따로 넣기 위해 sql.append를 사용했음, UPPER를 사용해 검색 시 입력한 값을 모두 대문자 처리한 뒤 LIKE를 이용해 해당 조건 문구를 포함한 모든 값을 가져올 수 있도록 함 
																	//状況に応じて条件を別に入れるためにsql.appendを使用した、UPPERを使用して検索時に入力した値をすべて大文字処理した後、LIKEを利用して該当条件の文言を含むすべての値を取得できるようにする
			params.add("%" + conditions.get("product_id") + "%");	//값을 더할 때 "%"를 앞 뒤로 더하면 앞뒤 공백이 없어진채료 깔끔하게 반환됨
																	//値を足すときに"%"を前後に足すと前後の空白がなくなったまま料がきれいに返される
		}
		if (conditions.get("product_name") != null) {
			sql.append(" AND UPPER(p.product_name) LIKE UPPER(?)");//위와 같음
			params.add("%" + conditions.get("product_name") + "%");
		}
		if (conditions.get("category") != null) {
			sql.append(" AND UPPER(p.category) LIKE  UPPER(?)");//위와같음
			params.add("%" + conditions.get("category") + "%");
		}
		if (conditions.get("supplier_id") != null) {
			sql.append(" AND UPPER(p.supplier_id) LIKE UPPER(?)");//위와같음
			params.add("%" + conditions.get("supplier_id") + "%");
		}
		sql.append(" ORDER BY p.product_id");	//결과값들을 product 테이블의 product_id로 정렬
												//結果をproductテーブルのproduct_idでソート

		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) { //Stringbuilder인 sql은 String형태로 변환해주어야 prepareStatement에 들어갈수 있다.
																				//StringBuilderのsqlはString型に変換する必要があり、prepareStatementに渡せるようにする
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i)); 	//조건으로 나온 params의 사이즈만큼 for문을 돌려 값을 가져온다.(.get)
														//条件として得られたparamsのサイズ分for文を回して値を取得する(.get)
			}  
			try (ResultSet rs = pstmt.executeQuery()) { //pstmt 즉 PreparedStatement는 sql문을 db에 보내는 역할 그리고 .executeQuery는 DB에서 Select쿼리를 식행하는 역할이다.
														//pstmt、つまりPreparedStatementはSQL文をDBに送る役割を持ち、.executeQueryはDBでSelectクエリを実行する役割を持つ
														//ResultSet는 실행 결과 데이터를 다시 자바로 가져오는 역할을 한다.
														//ResultSetは実行結果データを再びJavaに持ってくる役割をする。
				List<Product> list = new ArrayList<>(); //이제 여기서 나온 데이터들을 담을 list라는 객체를 생성
														//ここで取得したデータを格納するlistというオブジェクトを作成
				while (rs.next()) {	//ResultSet으로 받아온 데이터가 없을때까지 while문으로 순번을 넘겨 p로 얻은 Product DB를 list에 추가한다. (list.add(p))
									//ResultSetで受け取ったデータがないまで、while文で順番を渡してpで得たProduct DBをlistに追加する。 (list.add(p))
					Product p = new Product(rs.getString("product_id"), rs.getString("product_name"),
							rs.getString("category"), rs.getInt("price"), rs.getString("supplier_id"), rs.getString("supplier_name"), rs.getString("product_status"));
					list.add(p);
				}
				return list;//이후 해당 list를 반환
			}
		}
	}

	
	public List<String> getCategoryList(Connection conn) { // 카테고리 드롭박스 생성을 위해 카테고리 정보를 얻기 위한 메서드 (カテゴリドロップダウンを作成するため、カテゴリ情報を取得するメソッド)

		List<String> categoryList = new ArrayList<>();//카테고리 정보를 저장할 List형태의 categoryList 객체 선언 (カテゴリ情報を格納するList型のcategoryListオブジェクトを宣言)

		try (PreparedStatement pstmt = conn
				.prepareStatement("SELECT DISTINCT category FROM product ORDER BY category")) { //category를 참고해 product 테이블에 있는 카테고리를 중복 없이(DISTINCT) 가져옴
																								//categoryを参照してproductテーブルのカテゴリを重複なし(DISTINCT)で取得
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				categoryList.add(rs.getString("category"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryList;

	}
	  
	  public List<String> getSupplierList(Connection conn) {
		  
		  List<String> supplierList = new ArrayList<>(); 	//supplier_id를 String으로 모을 List형태의 supplierList 객체 생성
		  													//supplier_idをStringとしてまとめるList型のsupplierListオブジェクトを作成
		  
		  try (PreparedStatement pstmt = conn.
		  prepareStatement("SELECT supplier_id FROM supplier_info ORDER BY supplier_id")){ 	//supplier_info 테이블에서 supplier_id를 얻어옴
			  																				//supplier_infoテーブルからsupplier_idを取得
		  ResultSet rs = pstmt.executeQuery(); //위 쿼리 실행 후 rs에 데이터 적용 (上記クエリ実行後、rsにデータ適用)
		  while (rs.next()) { //전과 같이 데이터 없을때 까지 반복 (以前と同様にデータがなくなるまで繰り返す)
			  supplierList.add(rs.getString("supplier_id")); //얻어온 supplier_id 추가(.add) (取得したsupplier_idを追加(.add))
			  System.out.println("DAO - supplier_id: " + rs.getString("supplier_id"));
			  } 
		  } catch (Exception e) {
				  e.printStackTrace(); //에러발생시 예외처리 (エラー発生時に例外処理)
				  } 
		  return supplierList; // supplier_id 모은 List 반환 (supplier_idをまとめたListを返す)
		  
		  }
	  
	 
	  public int updateProductStatus(Connection conn, Product product) throws SQLException {
		  try(PreparedStatement pstmt = conn.prepareStatement("UPDATE PRODUCT SET PRODUCT_STATUS = ? WHERE PRODUCT_ID = ?")){ 	// product 테이블의 product_status를 변경하는데 id를 체크해서 (아래 2번째 setString) 아이디가 같으면 그 변경된 상태를 저장하는것
			   																													//productテーブルのproduct_statusを変更する際、idを確認して(下の2番目のsetString)同じIDなら変更された状態を保存する
		      pstmt.setString(1, product.getProduct_status()); 	//쿼리의 첫번째 물음표에 상태 대입 (クエリの1つ目の?に状態を代入)
			  pstmt.setString(2, product.getProduct_id());		//쿼리의 두번째 물음표에 상품 id 대입 (クエリの2つ目の?に商品IDを代入)
			  return pstmt.executeUpdate();						//쿼리 실행 후 리턴 (クエリ実行後リターン)
		  }
	  }
	  
	  public Product getProductById(Connection conn, String productId) { //product_id를 바탕으로 해당 ID에 해당하는 상품 정보 가져오기 (product_idを基に該当IDの商品の情報を取得)
		  Product product = null;
		    String sql =  "SELECT p.product_id, p.product_name, p.category, p.price, p.supplier_id, s.supplier_name, p.product_status"
					+ " FROM product p JOIN supplier_info s ON p.supplier_id = s.supplier_id WHERE p.product_id = ?";
		    //현재 수정 화면에서 수정 가능한 요소는 상품명, 카테고리, 가격 뿐이지만 추후 변경 가능성을 고려해 기존 List에서 받아오던 모든 정보를 세팅해주는 방향으로 함

		    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		        pstmt.setString(1, productId);	//product_id와 일치하는 정보를 가져오기 위해 productId로 퀄 ?에 값을 적용
		        								//product_idと一致する情報を取得するため、productId
		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (rs.next()) { //쿼리 실행 후 받은 테이터가 있을 시(next) 실행 (クエリー実行後に受信したデータがある場合(next)実行)
		                product = new Product();//객체 생성하고
		                product.setProduct_id(rs.getString("product_id"));
		                product.setProduct_name(rs.getString("product_name"));
		                product.setCategory(rs.getString("category"));
		                product.setPrice(rs.getInt("price"));
		                product.setSupplier_id(rs.getString("supplier_id"));
		                product.setSupplier_name(rs.getString("supplier_name"));
		                product.setProduct_status(rs.getString("product_status"));
		            }	//set~~로 모든 값 적용
		            	//set~~ですべての値を適用
		        }
		    } catch (Exception e) {
		        e.printStackTrace();//에러 발생시 예외 처리 (エラー発生時例外処理)
		    }
		    return product;//이후 만드러진 product 값 반환 (その後作られたproduct値の返還)
	  }
	  
	  public int modifyProduct(Connection conn, Product product) throws SQLException { 	// 반환형이 int인 이유는 반환되는 executeeUpdate가 영향받은 행의 개수를 반환하기 떄문에 개수는 숮자 즉 int로 반환됨
		  																				//返還型がintである理由は、返還されるexecuteeUpdateが影響を受けた行の個数を返還するため、個数は数字intで返還される
		    String sql = "UPDATE product SET product_name = ?, category = ?, price = ?, supplier_id = ?, product_status = ? WHERE product_id = ?";
		    //각 테이블 값을 아래 set~~로 받아서 대입함
		    //이것도 jsp에 있는것 보다 수정값이 많은데 수정 항목 추후 수정 여부를 고려해 적용

		    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
		        pstmt.setString(1, product.getProduct_name());
		        pstmt.setString(2, product.getCategory());
		        pstmt.setInt(3, product.getPrice());
		        pstmt.setString(4, product.getSupplier_id());
		        pstmt.setString(5, product.getProduct_status());
		        pstmt.setString(6, product.getProduct_id()); 
		  
		        return pstmt.executeUpdate();//쿼리 실행값 리턴 (クエリ実行値リターン)
		    }
		}
}
