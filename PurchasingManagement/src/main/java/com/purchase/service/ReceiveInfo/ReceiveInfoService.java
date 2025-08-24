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
 * 입고(Receive) 서비스 레이어
 * 폼에서 받은 값을 검증하고, 트랜잭션으로 insert/상태변경을 안전하게 처리
 *
 * 入庫サービス層
 * 入力の検証を行い、トランザクションで insert / 状態変更 を安全に実行
 *
 * 체크 포인트: 주문 존재 여부, 주문-상품 매칭, 수량(0 이상), 미래 날짜 금지
 * チェックポイント: 注文の存在, 注文-商品マッチ, 数量(0以上), 未来日NG
 */
public class ReceiveInfoService {

    private final ReceiveInfoDao receiveDao = new ReceiveInfoDao();
    private final OrderSheetDao  orderDao   = new OrderSheetDao();
    private final ProductDao     productDao = new ProductDao();

    // ========= 등록 (VO 바로 받는 버전) =========
    /** 입고 등록. 호출 측에서 새 ID가 굳이 필요 없을 때 사용
     *  入庫登録。呼び出し側で新しいIDが不要なときに使う */
    public void addReceiveInfo(ReceiveInfo vo) {
        // 1) 기본 검증
        // 1) 基本チェック
        validateForInsertBasic(vo);

        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);

            // 2) DB 존재/매칭 체크 (새 DAO 추가 없이 SQL로 확인)
            // 2) DBの存在/マッチ確認（新規DAOなしでSQLチェック）
            ensureOrderExists(vo.getOrder_id());
            ensureOrderHasProduct(vo.getOrder_id(), vo.getProduct_id());

            // 3) INSERT
            receiveDao.insert(conn, vo);

            conn.commit();
        } catch (SQLException e) {
            // 실패하면 롤백
            // 失敗時はロールバック
            rollbackQuietly(conn);
            throw new RuntimeException(e);
        } finally {
            // 커넥션 정리
            // 接続クローズ
            closeQuietly(conn);
        }
    }

    /** 입고 등록 후 생성된 receive_id 반환
     *  入庫登録後に生成された receive_id を返す */
    public String addReceiveInfoAndGetId(ReceiveInfo vo) {
        addReceiveInfo(vo);
        return vo.getReceive_id();
    }

    // ========= 등록 (문자열 파라미터 버전) =========
    /** 폼에서 온 문자열을 그대로 받아 처리하기 좋은 버전
     *  フォームの文字列をそのまま受けて処理しやすい版 */
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

        // 값 세팅해서 VO 만들기
        // 値を詰めてVOを作る
        ReceiveInfo vo = new ReceiveInfo();
        vo.setOrder_id(orderId);
        vo.setProduct_id(productId);
        vo.setQuantity(qty);
        vo.setReceive_status(isBlank(status) ? "検収中" : status); // 필요하면 null로 두고 DBデフォルトでもOK
        vo.setNote(note);
        Date recvDate = Date.from(recvLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        vo.setReceive_date(recvDate);

        addReceiveInfo(vo);
        return vo;
    }

    // ========= 검색 / 필터 =========
    /** 조건 검색 (상품명/공급업체/기간/반품가능/숨김 포함)
     *  条件検索（商品名/仕入先/期間/返品可能/非表示含む） */
    public List<ReceiveInfo> findWithFilter(
            String productName, String supplierName,
            String fromDate, String toDate,
            boolean onlyAvailable, boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            List<ReceiveInfo> list = receiveDao.findWithFilter(
                    conn, productName, supplierName, fromDate, toDate, onlyAvailable, includeHidden);
            // SQL 그대로 쓰고, product_name 비어있는 행만 후처리로 보강
            // SQLはそのまま、product_name が空の行だけ後処理で補強
            receiveDao.enrichProductNames(conn, list);
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 입고 목록 (JOIN 버전)
     *  画面に必要な名前類をJOINで取る */
    public List<ReceiveInfo> getReceiveListJoin(boolean onlyAvailable, boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            List<ReceiveInfo> list = receiveDao.selectAllView(conn, onlyAvailable, includeHidden);
            // 화면 표기 전에 상품명 보강
            // 表示前に商品名を補強
            receiveDao.enrichProductNames(conn, list);
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 반품 가능 수량 고려 (>0 만) — 반품 생성 화면 등에서 사용
     *  返品可能数を考慮（>0のみ）— 返品作成画面などで利用 */
    public List<ReceiveInfo> getReceiveInfoWithReturnQty() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            List<ReceiveInfo> list = receiveDao.selectReceiveInfoWithReturnQty(conn);
            // 상품명 보강
            // 商品名の補強
            receiveDao.enrichProductNames(conn, list);
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** JOIN 없이 receive_info 전체
     *  JOINなしの全件 */
    public List<ReceiveInfo> getAllReceiveInfos() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            // 화면에서 상품명이 필요 없으면 그냥 그대로 사용
            // 商品名が不要ならそのまま返す
            return receiveDao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ========= 상태 변경 =========
    /** 단건 상태 변경
     *  単体のステータス更新 */
    public int updateStatus(String receiveId, String status) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return receiveDao.updateStatus(conn, receiveId, status);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 행별 상태 일괄 변경 (트랜잭션)
     *  行別ステータス一括変更（トランザクション） */
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

    /** 상태 선택지 (UI용)
     *  画面のセレクト用ステータス */
    public List<String> getReceiveStatusList() {
        return Arrays.asList("検収中", "正常", "入庫取消", "返品処理");
        // 통일안 예시: Arrays.asList("検収中", "受入済", "入荷取消", "返品処理");
    }

    // ========= 폼 준비 =========
    /** 폼 드롭다운 등에 필요한 목록 세팅
     *  フォームのドロップダウン用リストをセット */
    public void prepareFormLists(HttpServletRequest req) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            // 주문ID 목록: selectAllView 결과에서 order_id만 뽑고 중복 제거
            // 注文ID一覧: selectAllView の結果から order_id を抜き重複除去
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

            // 상품 목록: ProductDao 재사용
            // 商品一覧: ProductDao を再利用
            List<Product> products = productDao.selectAll(conn);
            req.setAttribute("productList", products); // JSP: ${p.product_id} / ${p.product_name}
        } catch (SQLException e) {
            throw new RuntimeException("フォーム用リスト取得に失敗しました。", e);
        }
    }

    // ========= 내부 유틸 =========
    /** insert 기본 검증
     *  insert の基本チェック */
    private void validateForInsertBasic(ReceiveInfo vo) {
        if (vo == null) throw new IllegalArgumentException("ReceiveInfo is null");
        if (isBlank(vo.getOrder_id()))   throw new IllegalArgumentException("order_id is required");
        if (isBlank(vo.getProduct_id())) throw new IllegalArgumentException("product_id is required");
        if (vo.getQuantity() < 0) throw new IllegalArgumentException("quantity must be >= 0");
        if (vo.getReceive_date() == null) throw new IllegalArgumentException("receive_date is required");
        if (isFuture(vo.getReceive_date())) throw new IllegalArgumentException("入庫日は未来日を指定できません。");
    }

    /** 주문 존재 확인 (간단 SQL)
     *  注文の存在チェック（簡単SQL） */
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

    /** 주문-상품 매칭 확인 (order_sheet → purchase_request.product_id)
     *  注文-商品マッチ確認（order_sheet → purchase_request.product_id） */
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

    // 날짜가 미래인지 체크
    // 日付が未来かチェック
    private static boolean isFuture(Date d) {
        LocalDate target = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return target.isAfter(LocalDate.now());
    }

    // 빈 문자열 체크
    // 空文字チェック
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    // 앞뒤 공백 제거하고 빈 값이면 null
    // 前後の空白を削って空なら null
    private static String safe(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    // 조용히 롤백
    // 静かにロールバック
    private static void rollbackQuietly(Connection c) {
        if (c != null) try { c.rollback(); } catch (Exception ignore) {}
    }
    // 오토커밋 원복
    // オートコミットを戻す
    private static void resetAutocommitQuietly(Connection c) {
        if (c != null) try { c.setAutoCommit(true); } catch (Exception ignore) {}
    }
    // 커넥션 닫기
    // 接続を閉じる
    private static void closeQuietly(Connection c) {
        if (c != null) try { c.close(); } catch (Exception ignore) {}
    }
}
