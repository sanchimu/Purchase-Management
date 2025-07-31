package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcUtil {
	
	public static void close(ResultSet rs) { // ResultSet을 안전하게 닫는 메서드(ResultSetを安全に閉じるメソッド)
		if(rs != null) { // ResultSet이 null이 아닐 경우에만 (rsがnullでない場合のみ処理)
			try {
				rs.close();
			} catch (SQLException ex) { // 예외가 발생해도 무시 (例外が発生しても何もしない)
			}
		}
	}
	
	public static void close(Statement stmt) { // Statement 또는 PreparedStatement를 안전하게 닫는 메서드(Statement（またはPreparedStatement）を安全に閉じるメソッド)
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
			}
		}
	}
	
	public static void close(Connection conn) { // Connection 을 안전하게 닫는 메서드 (Connectionを安全に閉じるメソッド)
		if(conn!=null) {
			try {
				conn.close();
			}catch (SQLException ex) {
			}
		}
	}
	
	public static void rollback(Connection conn) { // 롤백 처리 메서드 (ロールバックするメソッド)
		if(conn!=null) {
			try {
				conn.rollback(); // 롤백 실행=트랜잭션 취소 (トランザクションを取り消す)
			} catch (SQLException ex) {
			}
		}
	}
}
