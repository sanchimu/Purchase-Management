package com.purchase.service.ReceiveInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import javax.servlet.http.HttpServletRequest;

import com.purchase.dao.ReceiveInfo.ReceiveInfoDao;
import com.purchase.dao.order.OrderSheetDao;
import com.purchase.dao.product.ProductDao;
import com.purchase.dto.OrderSheetDTO;
import com.purchase.vo.Product;
import com.purchase.vo.ReceiveInfo;

import jdbc.connection.ConnectionProvider;

/**
 * 入庫(Receive) 서비스 계층
 * - 검증: 주문 존재, 주문-상품 매칭, 수량(>=0), 미래일 금지
 * - 트랜잭션: insert/일괄상태변경 등에서 commit/rollback 보장
 */
public class ReceiveInfoService {

    private final ReceiveInfoDao receiveDao = new ReceiveInfoDao();
    private final OrderSheetDao  orderDao   = new OrderSheetDao();
    private final ProductDao     productDao = new ProductDao();

    // ========= 등록 (VO 직접) =========
    /** 입고 등록 (void) — 호출부에서 ID가 필요 없을 때 */
    public void addReceiveInfo(ReceiveInfo vo) {
        // 1) 기본 검증
        validateForInsertBasic(vo);

        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            // 2) DB존재/매칭 검증 (OrderSheetDao에 메서드 추가 없이 SQL로 확인)
            ensureOrderExists(vo.getOrder_id());
            ensureOrderHasProduct(vo.getOrder_id(), vo.getProduct_id());

            // 3) INSERT
            receiveDao.insert(conn, vo);

            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new RuntimeException(e);
        } finally {
            closeQuietly(conn);
        }
    }

    /** 입고 등록 후 생성된 receive_id 반환 */
    public String addReceiveInfoAndGetId(ReceiveInfo vo) {
        addReceiveInfo(vo);
        return vo.getReceive_id();
    }

    // ========= 등록 (원시 파라미터) =========
    /** 문자열 파라미터 버전 — 폼에서 바로 호출하기 편함 */
    public ReceiveInfo addReceiveInfo(String orderId, String productId,
                                      String qtyStr, String dateStr, String status, String note) {
        if (isBlank(orderId))   throw new IllegalArgumentException("注文番号を選択してください。");
        if (isBlank(productId)) throw new IllegalArgumentException("商品を選択してください。");

        int qty;
        try { qty = Integer.parseInt(qtyStr); }
        catch (Exception e) { throw new IllegalArgumentException("数量は数値で入力してください。"); }
        if (qty < 0) throw new IllegalArgumentException("数量は0以上で入力してください。");

        LocalDate recvLocalDate;
        try { recvLocalDate = LocalDate.parse(dateStr); } // yyyy-MM-dd
        catch (Exception e) { throw new IllegalArgumentException("入庫日は yyyy-MM-dd 形式で入力してください。"); }
        if (recvLocalDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("入庫日は未来日を指定できません。");

        ReceiveInfo vo = new ReceiveInfo();
        vo.setOrder_id(orderId);
        vo.setProduct_id(productId);
        vo.setQuantity(qty);
        vo.setReceive_status(isBlank(status) ? "検収中" : status); // 필요시 null로 두면 DB 기본값 적용
        vo.setNote(note);
        Date recvDate = Date.from(recvLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        vo.setReceive_date(recvDate);

        addReceiveInfo(vo);
        return vo;
    }

    // ========= 조회/필터 =========
    public List<ReceiveInfo> findWithFilter(
            String productName, String supplierName,
            String fromDate, String toDate,
            boolean onlyAvailable, boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return receiveDao.findWithFilter(conn, productName, supplierName, fromDate, toDate, onlyAvailable, includeHidden);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ReceiveInfo> getReceiveListJoin(boolean onlyAvailable, boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return receiveDao.selectAllView(conn, onlyAvailable, includeHidden);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 返品考慮済みの入庫情報（available_to_return > 0 のみ） */
    public List<ReceiveInfo> getReceiveInfoWithReturnQty() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return receiveDao.selectReceiveInfoWithReturnQty(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** JOIN 없이 receive_info 전체 */
    public List<ReceiveInfo> getAllReceiveInfos() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return receiveDao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ========= 상태변경 =========
    public int updateStatus(String receiveId, String status) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return receiveDao.updateStatus(conn, receiveId, status);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 행별 상태 일괄 변경 (트랜잭션) */
    public int updateStatuses(String[] receiveIds, List<String> statuses) {
        if (receiveIds == null || receiveIds.length == 0) return 0;
        if (statuses == null || statuses.size() != receiveIds.length) {
            throw new IllegalArgumentException("ids와 statuses의 개수가 일치하지 않습니다.");
        }

        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            int updated = 0;
            for (int i = 0; i < receiveIds.length; i++) {
                String id = safe(receiveIds[i]);
                String st = safe(statuses.get(i));
                if (id == null || st == null) continue;
                updated += receiveDao.updateStatus(conn, id, st);
            }

            conn.commit();
            return updated;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new RuntimeException(e);
        } finally {
            resetAutocommitQuietly(conn);
            closeQuietly(conn);
        }
    }

    /** 상태 선택지 (UI용) — 필요시 '受入済'로 교체 가능 */
    public List<String> getReceiveStatusList() {
        return Arrays.asList("検収中", "正常", "入庫取消", "返品処理");
        // 권장 통일안: Arrays.asList("検収中", "受入済", "入荷取消", "返品処理");
    }

    // ========= 폼 준비 =========
    /** 폼 드롭다운 등에 필요한 목록 세팅 (DAO 새 메서드 추가 없이 동작) */
    public void prepareFormLists(HttpServletRequest req) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            // 주문ID 목록: selectAllView 결과에서 order_id만 추출(중복 제거)
            List<OrderSheetDTO> view = orderDao.selectAllView(conn, /*includeHidden*/ false);
            LinkedHashSet<String> ids = new LinkedHashSet<>();
            for (OrderSheetDTO d : view) {
                if (d.getOrder_id() != null) ids.add(d.getOrder_id());
            }
            List<OrderSheetDTO> orderList = new ArrayList<>();
            for (String id : ids) {
                OrderSheetDTO dto = new OrderSheetDTO();
                dto.setOrder_id(id);
                orderList.add(dto);
            }
            req.setAttribute("orderList", orderList); // JSP: ${o.order_id}

            // 상품 목록: ProductDao.selectAll(conn) 재사용
            List<Product> products = productDao.selectAll(conn);
            req.setAttribute("productList", products); // JSP: ${p.product_id} / ${p.product_name}
        } catch (SQLException e) {
            throw new RuntimeException("フォーム用リスト取得に失敗しました。", e);
        }
    }

    // ========= 내부 유틸 =========
    private void validateForInsertBasic(ReceiveInfo vo) {
        if (vo == null) throw new IllegalArgumentException("ReceiveInfo is null");
        if (isBlank(vo.getOrder_id()))   throw new IllegalArgumentException("order_id is required");
        if (isBlank(vo.getProduct_id())) throw new IllegalArgumentException("product_id is required");
        if (vo.getQuantity() < 0) throw new IllegalArgumentException("quantity must be >= 0");
        if (vo.getReceive_date() == null) throw new IllegalArgumentException("receive_date is required");
        if (isFuture(vo.getReceive_date())) throw new IllegalArgumentException("入庫日は未来日を指定できません。");
    }

    /** 주문 존재 여부 — Service 내부 SQL로 검증 (DAO 변경 불필요) */
    private void ensureOrderExists(String orderId) {
        final String sql = "SELECT 1 FROM order_sheet WHERE order_id=? AND NVL(row_status,'A')='A'";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new IllegalArgumentException("注文番号が存在しません。");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 주문-상품 매칭 — order_sheet → purchase_request.product_id 검사 */
    private void ensureOrderHasProduct(String orderId, String productId) {
        final String sql =
            "SELECT 1 " +
            "  FROM order_sheet os " +
            "  JOIN purchase_request pr ON pr.request_id = os.request_id " +
            " WHERE os.order_id = ? " +
            "   AND pr.product_id = ? " +
            "   AND NVL(os.row_status,'A')='A' " +
            "   AND NVL(pr.row_status,'A')='A'";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            ps.setString(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new IllegalArgumentException("選択した注文に該当商品が含まれていません。");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isFuture(Date d) {
        LocalDate target = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return target.isAfter(LocalDate.now());
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private static String safe(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private static void rollbackQuietly(Connection c) {
        if (c != null) try { c.rollback(); } catch (Exception ignore) {}
    }
    private static void resetAutocommitQuietly(Connection c) {
        if (c != null) try { c.setAutoCommit(true); } catch (Exception ignore) {}
    }
    private static void closeQuietly(Connection c) {
        if (c != null) try { c.close(); } catch (Exception ignore) {}
    }
}

