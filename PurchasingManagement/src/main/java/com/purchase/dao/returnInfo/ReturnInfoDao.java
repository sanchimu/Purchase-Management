package com.purchase.dao.returnInfo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.purchase.vo.ReceiveInfo;
import com.purchase.vo.ReturnInfo;

import jdbc.JdbcUtil;

public class ReturnInfoDao {
	public ReturnInfo insert(Connection conn, ReturnInfo returnInfo)throws SQLException {
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			int seqNum = 0;
			pstmt = conn.prepareStatement("select return_info_seq.nextval from dual"); // 시퀀스 값을 가져오기위해한건데 쿼리에는 무조건 from이 필요해서 dual이라는 오라클에서 제공하는 가상 테이블을 활용
			rs = pstmt.executeQuery(); // DB값(시퀀스 값) 받음
			if (rs.next()) {//시퀀스가 있을 경우 seqNum이 0 -> 1로 바뀜
				seqNum = rs.getInt(1);
			}
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt); // ResultSet과 PreparedStatement를 사용했으니 close 처리로 마감 (안해도 기능 문제는 없지만 메모리 누수 등의 손해 발생)

			
			String returnId = String.format("RI%02d", seqNum); 	// returnInfo_id 만들기: "RI" + 3자리 숫자 (ex. RI001)
			returnInfo.setReturn_id(returnId);;					// 생성된 returnInfo_id를 세팅	
			
			pstmt = conn.prepareStatement("INSERT INTO RETURN_INFO VALUES (?,?,?,?,?,?)"); //insert into로 테이블에 DB생성
			pstmt.setString(1, returnInfo.getReturn_id());
			pstmt.setString(2, returnInfo.getReceive_id());
			pstmt.setString(3, returnInfo.getProduct_id());
			pstmt.setInt(4, returnInfo.getQuantity());
			pstmt.setString(5, returnInfo.getReason());
			pstmt.setDate(6,  new Date( returnInfo.getReturn_date() .getTime()) );
			int insertedCount = pstmt.executeUpdate();	//DB에 영향 받은 개수를 insertedCount에 저장
			//executeUpdate와 executeQuery랑 헷갈리지 않도록 조심해야할것같음
			
			  if (insertedCount > 0) {//영향 받은 행 개수 즉, 생성된 행 개수가 있을 시( >0 )
	                stmt = conn.createStatement();
	                rs = stmt.executeQuery(
	                        "select * from (select return_id from return_info order by return_id desc) where rownum = 1"); //product_id로 product 테이블에서 product_id를 찾는데 내림차순(desc, 오름차순은 asc) 으로 정렬해서 찾음, rownum은 그중 첫번째(1)
	                if (rs.next()) {
	                    return new ReturnInfo(returnInfo.getReturn_id(), returnInfo.getReceive_id(), returnInfo.getProduct_id(), returnInfo.getQuantity(), 
	                    		returnInfo.getReason(), returnInfo.getReturn_date());//여기서 next는 생성이 되었다는 뜻(값이 있다는것이니) 그럼 이제 값을 리턴해줘서 받아옴
	                }
			  }
			return null;
		}finally { //마지막으로 close처리
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
            JdbcUtil.close(stmt);
		}
	}
	
	public int delete (Connection conn, String rno) throws SQLException{
		try(PreparedStatement pstmt = conn.prepareStatement("delete from return_info where return_id = ?")){ //ID값을 찾아서 해당 DB를 삭제하는 쿼리
			pstmt.setString(1, rno);
			return pstmt.executeUpdate();
		}
	}
	
	
	public List<ReturnInfo> selectAll(Connection conn) throws SQLException {//모든 DB리스트를받아오기 위한 메서드
		String sql = "SELECT r.return_id, r.receive_id, r.product_id, p.product_name, r.quantity, r.reason, r.return_date "
				+ "FROM return_info r JOIN product p ON r.product_id = p.product_id ORDER BY r.return_id";
		List<ReturnInfo> returnInfoList = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) { //해당 try문 사용방식은 productDao에서 설명함
			while (rs.next()) {//rs로 들어온 DB가 나오지 않을 때 까지 while문 반복
				ReturnInfo r = new ReturnInfo(rs.getString("return_id"), rs.getString("receive_id"),
						rs.getString("product_id"), rs.getInt("quantity"), rs.getString("reason"),
						rs.getDate("return_date"), rs.getString("product_name"));
				returnInfoList.add(r);//반복때 마다 list에 returnInfoList 객체 저장
			}
		}
		return returnInfoList;//저장된 정보들 반환
	}
	
	public List<ReturnInfo> selectByConditions(Connection conn, Map<String, String> conditions) throws SQLException {
		StringBuilder sql = new StringBuilder("SELECT  r.return_id, r.receive_id, r.product_id, p.product_name, r.quantity, r.reason, r.return_date "
				+ "FROM return_info r JOIN product p ON r.product_id = p.product_id WHERE 1=1");
		List<Object> params = new ArrayList<>();//조건으로 나온 값들을 저장할 List 변수 params

		if (conditions.get("return_id") != null) {
			sql.append(" AND UPPER (r.return_id) LIKE UPPER(?)");//상황에 따라 조건을 따로 넣기 위해 sql.append를 사용했음, UPPER를 사용해 검색 시 입력한 값을 모두 대문자 처리한 뒤 LIKE를 이용해 해당 조건 문구를 포함한 모든 값을 가져올 수 있도록 함
			params.add("%" + conditions.get("return_id") + "%");//값을 더할 때 "%"를 앞 뒤로 더하면 앞뒤 공백이 없어진채료 깔끔하게 반환됨
		}
		if (conditions.get("receive_id") != null) {
			sql.append(" AND UPPER (r.receive_id) LIKE UPPER(?)");//위와 같음
			params.add("%" + conditions.get("receive_id") + "%");
		}
		if (conditions.get("product_id") != null) {
			sql.append(" AND UPPER (r.product_id) LIKE UPPER(?)");//위와같음
			params.add("%" + conditions.get("product_id") + "%");
		}
		sql.append(" ORDER BY r.return_id");//결과값들을 return_info 테이블의 return_id로 정렬

		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) { //Stringbuilder인 sql은 String형태로 변환해주어야 prepareStatement에 들어갈수 있다.
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i)); //조건으로 나온 params의 사이즈만큼 for문을 돌려 값을 가져온다.(.get)
			}
			try (ResultSet rs = pstmt.executeQuery()) { //pstmt 즉 PreparedStatement는 sql문을 db에 보내는 역할 그리고 .executeQuery는 DB에서 Select쿼리를 식행하는 역할이다.
				//ResultSet는 실행 결과 데이터를 다시 자바로 가져오는 역할을 한다.
				List<ReturnInfo> list = new ArrayList<>(); //이제 여기서 나온 데이터들을 담을 list라는 객체를 생성
				while (rs.next()) {//ResultSet으로 받아온 데이터가 없을때까지 while문으로 순번을 넘겨 p로 얻은 Product DB를 list에 추가한다. (list.add(p))
					ReturnInfo ri = new ReturnInfo(rs.getString("return_id"), rs.getString("receive_id"),
							rs.getString("product_id"), rs.getInt("quantity"), rs.getString("reason"), rs.getDate("return_date"), rs.getString("product_name"));
					list.add(ri);
				}
				return list;//이후 해당 list를 반환
			}
		}
	}
	
	    
	

}
