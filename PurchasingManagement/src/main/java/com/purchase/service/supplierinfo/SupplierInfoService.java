package com.purchase.service.supplierinfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.purchase.dao.supplierinfo.SupplierInfoDao;
import com.purchase.vo.SupplierInfo;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

// Service 계층. Controller와 DAO 사이에서 비즈니스 로직 + 트랜잭션 처리 담당
// Controller에서 요청한 작업 수행. DB트랜잭션 관리, DAO 호출해서 DB작업 실행
public class SupplierInfoService {

    private SupplierInfoDao supplierDao = new SupplierInfoDao();
    // Service는 직접 SQL을 실행하지 않고 DAO를 통해 실행

    // 공급업체 추가
    public void insertSupplier(SupplierInfo supplier) {
    	// 매개변수 SupplierInfo 는 공급업체 정보를 담는 VO 객체.
    	// supplier_id, supplier_name 등등 포함
        Connection conn = null; // DB와 연결할 Connection 객체 선언. 처음에 null 로 초기화
        try { // 예외 처리 시작. DB작업 중 SQLException이 발생할수 있으므로 try-catch-finally로 감싸서 처리
            conn = ConnectionProvider.getConnection();
            // DB 연결 생성
            conn.setAutoCommit(false);
            // 자동 커밋 기능 끄기

            supplierDao.insert(conn, supplier);
            // DAO 를 사용해 공급업체 정보 supplier 를 DB에 insert. conn을 함께 전달해서
            // 같은 트랜잭션 안에서 실행되게 함

            conn.commit(); // 트랜잭션 커밋. SQL작업들을 DB에 반영
        } catch (SQLException e) { //DB 작업 중 오류가 발생했을 때 실행되는 블록
            JdbcUtil.rollback(conn); // 트랜잭션 롤백. SQL 작업들 취소
            throw new RuntimeException(e); // 발생한 SQLException을 RuntimeException으로 감싸서 던짐
        } finally { // 무조건 실행
            JdbcUtil.close(conn); // DB연결 종료
        }
    }

    // 공급업체 삭제
    public void deleteSupplier(String supplierId) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            supplierDao.delete(conn, supplierId);

            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    // 공급업체 검색
    public List<SupplierInfo> searchSuppliers(String name) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return supplierDao.selectByName(conn, name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    // 공급업체 목록 조회
    public List<SupplierInfo> getSupplierList() {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return supplierDao.selectAll(conn); 
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
}
