package com.purchase.service.ReceiveInfo;

import com.purchase.dao.ReceiveInfo.ReceiveInfoDao;
import com.purchase.vo.ReceiveInfo;
import jdbc.connection.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ReceiveInfoService {
    private final ReceiveInfoDao dao = new ReceiveInfoDao();

    /** 入庫登録 
     *  입고 등록 */
    public void addReceiveInfo(ReceiveInfo receiveInfo) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            dao.insert(conn, receiveInfo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** （互換用）単純な全件リスト 
     *  (호환용) 단순 전체 목록 조회 */
    public List<ReceiveInfo> getAllReceiveInfos() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JOINを利用した一覧取得
     * JOIN을 활용한 목록 조회
     * @param onlyAvailable   trueなら返品可能数量 > 0 のみ  
     *                        true면 반품 가능 수량 > 0 인 데이터만
     * @param includeHidden   trueなら row_status='X' のような非表示データも含む  
     *                        true면 숨김 데이터(row_status='X')도 포함
     */
    public List<ReceiveInfo> getReceiveListJoin(boolean onlyAvailable, boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            // ➜ ReceiveInfoDao.selectAllView() を呼び出し
            // DAO의 selectAllView 호출
            return dao.selectAllView(conn, onlyAvailable, includeHidden);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** （互換用）返品可能数量だけが必要な画面で使用 
     *  (호환용) 반품 가능 수량만 필요한 화면에서 사용 */
    public List<ReceiveInfo> getReceiveInfoWithReturnQty() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectReceiveInfoWithReturnQty(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    /** （互換用）商品名/仕入先名 + 返品可能数量が必要な画面で使用 
     *  (호환용) 상품명/공급업체명 + 반품 가능 수량이 필요한 화면에서 사용 */
    public List<ReceiveInfo> getReceiveInfoWithNamesAndReturnQty() {
        // 同じくJOIN結果を利用（このメソッド名を期待しているJSP/ハンドラーがあるため維持）
        // JOIN 결과를 재사용 (JSP/핸들러에서 이 메서드명을 기대하므로 유지)
        return getReceiveListJoin(true, false);
    }

    /** ドロップダウン用の業務ステータスセット 
     *  드롭다운용 업무 상태 목록 */
    public List<String> getReceiveStatusList() {
        return Arrays.asList("検収中", "正常", "入庫キャンセル", "返品処理");
        // 검수중, 정상, 입고 취소, 반품 처리
    }

    /** 選択項目のステータスを一括更新 
     *  선택 항목들의 상태를 일괄 업데이트 */
    public void updateStatuses(String[] ids, List<String> statuses) {
        if (ids == null || ids.length == 0) return;
        try (Connection conn = ConnectionProvider.getConnection()) {
            conn.setAutoCommit(false); // トランザクション開始 / 트랜잭션 시작
            try {
                for (int i = 0; i < ids.length; i++) {
                    dao.updateStatus(conn, ids[i], statuses.get(i));
                }
                conn.commit(); // コミット / 커밋
            } catch (SQLException ex) {
                conn.rollback(); // ロールバック / 롤백
                throw ex;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    /** receiveId から productId を取得 
     *  receiveId로부터 productId 조회 */
    public String getProductIdByReceiveId(String receiveId) throws Exception {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.getProductIdByReceiveId(conn, receiveId);
        }
    }
}
