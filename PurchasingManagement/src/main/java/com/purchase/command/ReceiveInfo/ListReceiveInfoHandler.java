package com.purchase.command.ReceiveInfo;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;
import mvc.command.CommandHandler;

/**
 * 入庫一覧の表示と検索を受け持つハンドラー
 * 条件を拾って整えたらサービスに渡す → 結果をJSPへ
 *
 * 입고 목록 보여주기 + 검색 담당
 * 조건 모아서 다듬고 서비스 호출 → 결과를 JSP로 전달
 */
public class ListReceiveInfoHandler implements CommandHandler {

    // 実処理はサービスに任せる。ここは橋渡し
    // 실제 처리는 서비스에 맡기고, 여기는 중간다리 역할
    private final ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        // 1) 返品可能だけ見るモードかどうか（mode=avail なら true）
        // 1) 반품 가능만 볼 건지 확인(mode=avail이면 true)
        String mode = req.getParameter("mode");
        boolean onlyAvailable = "avail".equalsIgnoreCase(mode);

        // 2) 検索パラメータを受け取る（JSPのnameと1:1）
        // 2) 검색 파라미터 받기(JSP name과 1:1 매칭)
        String productName  = t(req.getParameter("productName"));
        String supplierName = t(req.getParameter("supplierName"));
        String fromDate     = t(req.getParameter("fromDate")); // 形式: YYYY-MM-DD
        String toDate       = t(req.getParameter("toDate"));   // 形式: YYYY-MM-DD

        // 2-1) 期間が逆（from > to）なら入れ替え。とりあえず使える形に直す
        // 2-1) 기간이 거꾸로면(from > to) 값 교체해서 사용 가능하게
        if (fromDate != null && toDate != null && fromDate.compareTo(toDate) > 0) {
            String tmp = fromDate;
            fromDate = toDate;
            toDate = tmp;
        }

        // 3) 非表示データも含めるか（現状仕様そのまま）
        // 3) 숨김 데이터 포함할지(현재 사양 유지)
        boolean includeHidden = true;

        // 4) サービス呼び出し。フィルタの有無で取り方を分ける
        // 4) 서비스 호출. 필터 존재 여부에 따라 조회 방식 분기
        List<ReceiveInfo> receiveList;

        boolean noFilters =
                (productName == null && supplierName == null && fromDate == null && toDate == null);

        if (onlyAvailable && noFilters) {
            // 返品可能のみ + 条件なし → 返品数を考慮した専用取得
            // 반품 가능 전용 + 무필터 → 반품 수량 고려 전용 목록
            receiveList = service.getReceiveInfoWithReturnQty();
        } else if (noFilters) {
            // 条件なし → 全件JOIN（商品名/仕入先名つき）
            // 무필터 → 전체 조인(상품명/공급업체명 포함)
            receiveList = service.getReceiveListJoin(false, includeHidden);
        } else {
            // 条件あり → 条件検索（onlyAvailableなら HAVING で絞る）
            // 필터 있음 → 조건 검색(onlyAvailable면 HAVING로 좁힘)
            receiveList = service.findWithFilter(
                    productName, supplierName, fromDate, toDate, onlyAvailable, includeHidden
            );
        }

        // 5) JSPへ渡すものをセット（一覧 / 状態リスト / モード / 入力値）
        // 5) JSP에 넘길 값 세팅(목록 / 상태리스트 / 모드 / 입력값 유지)
        req.setAttribute("receiveList", receiveList);
        req.setAttribute("receiveStatusList", service.getReceiveStatusList());
        req.setAttribute("mode", mode);

        // 検索フォームの入力値はそのまま戻しておく
        // 검색 폼 입력값 유지
        req.setAttribute("productName",  productName);
        req.setAttribute("supplierName", supplierName);
        req.setAttribute("fromDate",     fromDate);
        req.setAttribute("toDate",       toDate);

        // 一覧JSPへフォワード
        // 목록 화면으로 포워드
        return "/WEB-INF/view/receiveInfoList.jsp";
    }

    /**
     * 文字列ユーティリティ
     * null / 空白だけ を nullに寄せる。その他はtrimで余計なスペース削る
     *
     * 문자열 유틸
     * null / 공백뿐인 문자열은 null로, 그 외는 trim해서 반환
     */
    private String t(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}

