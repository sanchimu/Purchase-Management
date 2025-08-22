package com.purchase.service.supplierinfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.purchase.dao.supplierinfo.SupplierInfoDao;
import com.purchase.vo.SupplierInfo;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;


// 공급업체 관련 비즈니스 로직 처리하는 클래스
// DB에 직접 접근하지는 않고 DAO 를 사용해서 중간 역할을 함
public class SupplierInfoService {

    private final SupplierInfoDao supplierDao = new SupplierInfoDao();
    // DB랑 연결하기 위해 만든 객체. 

    public void insertSupplier(SupplierInfo supplier) {
    	// 공급업체 정보를 DB에 등록하기 위한 메서드
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            // DB연결 가져오는 코드
            supplierDao.insert(conn, supplier);
            // DAO 에 insert시키는 부분
            // supplierDao를 통해 DB에 데이터 넣음
            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new RuntimeException(e);
            // 중간에 오류가 나면 롤백
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public List<SupplierInfo> getSupplierList() {
    	// 모든 공급업체 정보를 리스트로 가져옴
        try (Connection conn = ConnectionProvider.getConnection()) {
            return supplierDao.selectAll(conn);
            // DAO가 DB에서 공급업체 전부 읽어옴
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // 에러나면 예외 던짐
    }

    public List<SupplierInfo> searchSuppliersByName(String name) {
    	// name이 포함된 공급업체만 검색
        try (Connection conn = ConnectionProvider.getConnection()) {
            return supplierDao.selectByName(conn, name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSupplierStatus(SupplierInfo supplier) {
    	// 공급업체의 상태를 수정하는 역할
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            supplierDao.updateSupplierStatus(conn, supplier);
            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new RuntimeException(e);
            // 실패시 롤백
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public void deleteSuppliers(String[] supplierIds) {
    	// supplierIds : 삭제할 공급업체의 ID들을 모아놓은 배열
    	// 여러개의 공급업체 한 번에 지울 수 있도록 하는 메서드
        if (supplierIds == null || supplierIds.length == 0) return;
        // 만약 supplierIds가 null 이거나 길이가 0이면 아무것도 안 하고 메서드 종료
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            for (String id : supplierIds) {
            	// supplierIds 배열 안에 든 문자열을 하나씩 꺼내서 id 변수에 담음
                supplierDao.delete(conn, id);
            }
            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
    }

    public SupplierInfo getSupplierById(String supplierId) {
    	// 특정 supplierId 값 받아서 그 공급업체 정보 가져오는 메서드
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            return supplierDao.selectById(conn, supplierId);
            // selectById메서드를 호출해서 ID에 맞는 행을 가져와서 SupplierInfo 객체에 담아줌
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    public void updateSupplier(SupplierInfo supplier) {
    	// 공급업체 수정하는 메서드
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            supplierDao.update(conn, supplier);
            // supplier 객체에 들어있는 값으로 DB 행 업데이트
            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }
    
    public void updateStatus(String supplierId, String newStatus) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();   
            supplierDao.updateStatus(conn, supplierId, newStatus);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }


}
