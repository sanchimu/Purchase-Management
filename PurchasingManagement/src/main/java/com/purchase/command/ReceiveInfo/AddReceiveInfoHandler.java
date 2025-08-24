package com.purchase.command.ReceiveInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;
import mvc.command.CommandHandler;

/**
 * 入庫の新規登録を受け取るハンドラー
 * フォームの値を集めて → ざっとチェック → 登録 → できたら一覧へリダイレクト
 *
 * 입고 신규 등록 처리용 핸들러
 * 폼 값 모아서 → 간단히 검증 → 저장 → 성공하면 목록으로 리다이렉트
 *
 * 화면에서 온 데이터를 서비스에 넘겨서 처리. 성공이면 목록으로, 실패면 폼으로 다시.
 */
public class AddReceiveInfoHandler implements CommandHandler {

    // 비즈니스 로직은 서비스에게 맡긴다. 핸들러는 전달·결과만 챙긴다
    // 検証やDB登録はサービス層に任せる。ここは橋渡し役
    private final ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 요청 본문을 UTF-8로 읽기. 한글,일본어 깨짐 방지
        // リクエストの文字コードをUTF-8に。文字化けしないように
        req.setCharacterEncoding("UTF-8");

        // 폼에서 넘어온 값 모으기. getParameter의 키는 input name과 같아야 한다
        // フォームのnameと同じキーでパラメータ取得
        String orderId   = req.getParameter("order_id");
        String productId = req.getParameter("product_id");
        String qtyStr    = req.getParameter("quantity");
        String dateStr   = req.getParameter("receive_date"); // 기대/想定: yyyy-MM-dd
        String status    = req.getParameter("receive_status"); // 화면에서 기본값을 "検収中"로 줄 수 있음
        String note      = req.getParameter("note");

        try {
            // 검증 + 등록은 서비스로. 여기서는 결과만 받는다
            // 入力チェックと登録はサービスに任せる。ここは投げて受け取るだけ
            ReceiveInfo created = service.addReceiveInfo(orderId, productId, qtyStr, dateStr, status, note);

            // 성공하면 목록으로 보낸다. 메시지는 쿼리스트링에 얹어서 토스트 등으로 쓰면 편함
            // 成功時は一覧へリダイレクト。メッセージはクエリに載せてトースト表示などに使う
            res.sendRedirect(req.getContextPath() + "/listReceiveInfos.do?msg=" +
                    java.net.URLEncoder.encode("登録に成功しました", "UTF-8")); // 다국어는 URL 인코딩 필요
            return null; // 리다이렉트 했으니 뷰 반환 없음。ここでJSPを返さない

        } catch (IllegalArgumentException be) {
            // 입력값에 문제가 있을 때. 에러 메시지 보여주고 폼으로 다시
            // 入力不正のとき。メッセージを出してフォーム再表示
            req.setAttribute("errorMsg", be.getMessage());

            // 셀렉트박스 같은 옵션 리스트 다시 채우기
            // セレクトなどの選択肢を詰め直す
            service.prepareFormLists(req);

            // 사용자가 쓴 값 최대한 복구. 다시 타이핑 줄이자
            // 入力値をできるだけ復元して手戻りを減らす
            ReceiveInfo ri = new ReceiveInfo();
            ri.setOrder_id(orderId);
            ri.setProduct_id(productId);
            try {
                ri.setQuantity(Integer.parseInt(qtyStr));
            } catch(Exception ignore){
                // 숫자 변환 실패해도 그냥 둔다. 폼에서 다시 입력하면 된다
                // 数値変換に失敗しても落とさない。フォームで再入力してもらう
            }
            ri.setReceive_status(status);
            // 날짜는 문자열 그대로 JSP value에 넘긴다. 여기서 Date로 바꾸지 않는다
            // 日付は文字列のままJSPへ渡す。ここでDate変換しない
            req.setAttribute("ri", ri);

            // 등록 폼 다시 보여주기
            // 元のフォームに戻す
            return "/WEB-INF/view/receiveInfoForm.jsp";

        } catch (Exception e) {
            // 서버 쪽 예기치 않은 에러. 너무 세세한 내용은 화면에 안 보이고, 부드러운 문구만
            // 想定外のサーバエラー。詳細はログへ、画面はやさしいメッセージだけ
            req.setAttribute("errorMsg", "サーバーエラーが発生しました。（登録失敗）");

            // 옵션 리스트 다시 세팅
            // フォームの選択肢を再セット
            service.prepareFormLists(req);

            // 폼 다시 띄우기. 사용자가 바로 고쳐서 보낼 수 있게
            // フォーム再表示。すぐ直して送れるように
            return "/WEB-INF/view/receiveInfoForm.jsp";
        }
    }
}
