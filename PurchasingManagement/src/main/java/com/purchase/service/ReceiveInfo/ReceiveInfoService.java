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

    /** 入庫登録 */
    public void addReceiveInfo(ReceiveInfo receiveInfo) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            dao.insert(conn, receiveInfo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** （互換用）単純な全件リスト */
    public List<ReceiveInfo> getAllReceiveInfos() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JOINを利用した一覧取得
     * @param onlyAvailable   trueなら返品可能数量 > 0 のみ
     * @param includeHidden   trueなら row_status='X' のような非表示データも含む
     */
    public List<ReceiveInfo> getReceiveListJoin(boolean onlyAvailable, boolean includeHidden) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            // ➜ ReceiveInfoDao に selectAllView(conn, onlyAvailable, includeHidden) が実装されている必要あり
            return dao.selectAllView(conn, onlyAvailable, includeHidden);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** （互換用）返品可能数量だけが必要な画面で使用 — 既存ハンドラーがこのメソッドを呼ぶ場合に対応 */
    public List<ReceiveInfo> getReceiveInfoWithReturnQty() {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.selectReceiveInfoWithReturnQty(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /** （互換用）商品名/仕入先名 + 返品可能数量が必要な画面で使用 — 既存ハンドラー対応 */
    public List<ReceiveInfo> getReceiveInfoWithNamesAndReturnQty() {
        // 同じくJOIN結果を利用（このメソッド名を期待しているJSP/ハンドラーがあるため維持）
        return getReceiveListJoin(true, false);
    }

    /** ドロップダウン用の業務ステータスセット */
    public List<String> getReceiveStatusList() {
        return Arrays.asList("検収中", "正常", "入庫キャンセル", "返品処理");
    }

    /** 選択項目のステータスを一括更新 */
    public void updateStatuses(String[] ids, List<String> statuses) {
        if (ids == null || ids.length == 0) return;
        try (Connection conn = ConnectionProvider.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (int i = 0; i < ids.length; i++) {
                    dao.updateStatus(conn, ids[i], statuses.get(i));
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    /** receiveId から productId を取得 */
    public String getProductIdByReceiveId(String receiveId) throws Exception {
        try (Connection conn = ConnectionProvider.getConnection()) {
            return dao.getProductIdByReceiveId(conn, receiveId);
        }
    }
}

