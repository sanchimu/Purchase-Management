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
import java.util.Map;
import java.util.HashMap;

/**
 * 발주서 서비스 / 発注書サービス
 * 목록/옵션 조회, 등록/상태변경 트랜잭ション管理
 * 화면/DB 상태값 표준화(日本語) 유지
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
    
    /** 검색(조인뷰) – 파라미터 Map 입력 / 検索（結合ビュー） パラメータMap */
    public List<OrderSheetDTO> searchView(Map<String, String> params) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            Map<String, String> p = normalize(params);
            List<OrderSheetDTO> list = dao.selectViewByConditions(conn, p);
            if (list == null || list.isEmpty()) {
                boolean includeHidden = "1".equals(p.getOrDefault("includeHidden", "0"));
                return dao.selectAllView(conn, includeHidden); // 결과 없으면 전체
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 검색(조인뷰) – 편의 오버로드 / 検索（結合ビュー） 便利オーバーロード */
    public List<OrderSheetDTO> searchView(
            String orderId,
            String requestId,
            String supplierId,
            String orderStatus,
            String fromDate,   // yyyy-MM-dd
            String toDate,     // yyyy-MM-dd
            boolean includeHidden
    ) {
        Map<String, String> params = new HashMap<>();
        putIfNotBlank(params, "order_id", orderId);
        putIfNotBlank(params, "request_id", requestId);
        putIfNotBlank(params, "supplier_id", supplierId);
        putIfNotBlank(params, "order_status", orderStatus);
        putIfNotBlank(params, "from_date", fromDate);
        putIfNotBlank(params, "to_date", toDate);
        params.put("includeHidden", includeHidden ? "1" : "0");
        return searchView(params);
    }

    /* 내부 유틸 / 内部ユーティリティ */

    private Map<String, String> normalize(Map<String, String> in) {
        Map<String, String> out = new HashMap<>();
        if (in == null) return out;
        in.forEach((k, v) -> {
            if (v != null) {
                String t = v.trim();
                if (!t.isEmpty()) out.put(k, t);
            }
        });
        if (!out.containsKey("includeHidden")) out.put("includeHidden", "0");
        return out;
    }

    private void putIfNotBlank(Map<String, String> m, String key, String val) {
        if (val == null) return;
        String t = val.trim();
        if (!t.isEmpty()) m.put(key, t);
    }

}
