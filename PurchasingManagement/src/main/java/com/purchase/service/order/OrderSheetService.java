package com.purchase.service.order;

import com.purchase.dao.order.OrderSheetDao;
import com.purchase.dto.OrderSheetDTO;
import com.purchase.vo.OrderSheet;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 발주서 서비스 / 発注書サービス
 * - 목록/옵션 조회, 등록/상태변경 트랜잭ション管理
 * - 화면/DB 상태값 표준화(日本語) 유지
 *
 * 모든 공개 메서드는 SQLException을 직접 던지지 않고 RuntimeException으로 래핑합니다.
 * すべての公開メソッドは SQLException を投げず、RuntimeException にラップします。
 */
public class OrderSheetService {

    private final OrderSheetDao dao = new OrderSheetDao();

    /** 목록(조인) 조회 / 一覧（結合）取得 */
    public List<OrderSheetDTO> getAllView(boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectAllView(conn, includeHidden);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 드롭다운용 요청 옵션 / ドロップダウン用申請オプション */
    public List<OrderSheetDTO> getRequestOptions() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectRequestOptions(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 요청ID → 공급업체ID (서버 검증/자동결정) / 申請ID→供給業者ID（サーバ側検証・自動決定） */
    public String findSupplierIdByRequestId(String requestId) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.findSupplierIdByRequestId(conn, requestId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 다음 발주ID 생성 / 次の発注ID生成 */
    public String generateNextOrderId() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.nextOrderId(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 발주 등록(트랜잭션) / 発注登録（トランザクション） */
    public void insert(OrderSheet order) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            dao.insert(conn, order);

            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /** 업무상태 저장(단건) / 業務ステータス保存（単件） */
    public void updateOrderStatus(OrderSheet os) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            dao.updateStatus(conn, os);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 표시상태(A/X) 일괄 변경 / 表示状態（A/X）一括変更 */
    public void setRowStatusBulk(String[] ids, String rowStatus) {
        if (ids == null || ids.length == 0) return;
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            for (String id : ids) {
                if (id != null && !id.isBlank()) {
                    dao.updateRowStatus(conn, id.trim(), rowStatus);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            JdbcUtil.rollback(conn);
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /**
     * 업무상태 허용 세트(일본어) / 業務ステータス許容セット（日本語）
     * DB CHECK と 1:1 一致:
     *   ('発注依頼','承認','発注','一部入荷','完了','取消','保留')
     */
    public List<String> getOrderStatusList() {
        return Collections.unmodifiableList(
            Arrays.asList("発注依頼","承認","発注","一部入荷","完了","取消","保留")
        );
    }
}
