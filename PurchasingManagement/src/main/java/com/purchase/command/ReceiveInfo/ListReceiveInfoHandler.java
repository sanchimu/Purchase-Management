package com.purchase.command.ReceiveInfo;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;
import mvc.command.CommandHandler;

/**
 * 入庫情報一覧の表示／検索を担当するハンドラー
 * - 検索条件の取得 → 必要に応じて整形（期間の前後入替など）→ サービス呼び出し → 一覧JSPへ
 *
 * 입고 정보 목록 표시/검색을 담당하는 핸들러
 * - 검색 조건 수집 → 필요 시 정리(기간 역전 시 교체 등) → 서비스 호출 → 목록 JSP로 포워드
 */
public class ListReceiveInfoHandler implements CommandHandler {

    // サービス層：実データ取得・ビジネスロジックはここへ委譲
    // 서비스 레이어: 실제 데이터 조회/비즈니스 로직은 서비스에 위임
    private final ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        // 1) 返品可能分のみ表示するモードを取得（mode=avail のとき true）
        // 1) 반품 가능 건만 표시하는 모드 확인(mode=avail이면 true)
        String mode = req.getParameter("mode");
        boolean onlyAvailable = "avail".equalsIgnoreCase(mode);

        // 2) 検索パラメータ取得（JSPのname属性と1:1対応）
        // 2) 검색 파라미터 수집(JSP name과 1:1 매칭)
        String productName  = t(req.getParameter("productName"));
        String supplierName = t(req.getParameter("supplierName"));
        String fromDate     = t(req.getParameter("fromDate")); // 期待形式: YYYY-MM-DD / 기대 형식: YYYY-MM-DD
        String toDate       = t(req.getParameter("toDate"));   // 期待形式: YYYY-MM-DD / 기대 형식: YYYY-MM-DD

        // 2-1) 期間の前後が逆（from > to）の場合は入れ替え
        // 2-1) 기간 역전(from > to) 시 값 교체로 보정
        if (fromDate != null && toDate != null && fromDate.compareTo(toDate) > 0) {
            String tmp = fromDate;
            fromDate = toDate;
            toDate = tmp;
        }

        // 3) 非表示データを含めるか（現行仕様を踏襲）
        // 3) 숨김 데이터 포함 여부(현 규격 유지)
        boolean includeHidden = true;

        // 4) サービス呼び出し：フィルタ有無で取得方法を分岐
        // 4) 서비스 호출: 필터 여부에 따라 조회 방식 분기
        List<ReceiveInfo> receiveList;

        // [最適化] 条件が全く無く、かつ「返品可能モード」でもない場合は、
        // 既存の全件JOINメソッドでショートカット（パフォーマンス面の利点）
        //
        // [최적화] 조건이 전혀 없고 ‘반품가능 모드’도 아니면
        // 기존 전체 JOIN 메서드로 우회(성능상 이점)
        boolean noFilters = (productName == null && supplierName == null && fromDate == null && toDate == null);
        if (noFilters && !onlyAvailable) {
            receiveList = service.getReceiveListJoin(false, includeHidden);
        } else {
            receiveList = service.findWithFilter(
                productName, supplierName, fromDate, toDate, onlyAvailable, includeHidden
            );
        }

        // 5) JSPへ受け渡し（一覧データ／状態プルダウン／モード値）
        // 5) JSP로 전달(목록 데이터/상태 드롭다운/모드 값)
        req.setAttribute("receiveList", receiveList);
        req.setAttribute("receiveStatusList", service.getReceiveStatusList());
        req.setAttribute("mode", mode);

        // 一覧画面へフォワード
        // 목록 화면으로 포워드
        return "/WEB-INF/view/receiveInfoList.jsp";
    }

    /**
     * 文字列ユーティリティ：null/空文字を正規化して扱いやすくする
     * - null → null
     * - "  "（空白のみ）→ null
     * - それ以外 → trimした文字列
     *
     * 문자열 유틸: null/빈문자 정규화
     * - null → null
     * - "  " (공백만) → null
     * - 그 외 → trim 결과 반환
     */
    private String t(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}
