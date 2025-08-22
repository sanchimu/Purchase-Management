package com.purchase.command.ReceiveInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;
import mvc.command.CommandHandler;

/**
 * 入庫情報の新規登録を受け付けるハンドラー
 * - フォームから送られてきた値を取得 → 検証 → 登録 → 成功時は一覧へリダイレクト
 *
 * 입고 정보 신규 등록을 처리하는 핸들러
 * - 폼 파라미터 수집 → 검증 → 저장 → 성공 시 목록으로 리다이렉트
 */
public class AddReceiveInfoHandler implements CommandHandler {

    // ビジネスロジック層。検証・DB登録などはサービスへ委譲。
    // 비즈니스 로직 레이어. 검증/DB 저장은 서비스에 위임.
    private final ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 文字化け防止のためのエンコーディング指定（UTF-8）
        // 한글/일본어 깨짐 방지를 위한 요청 인코딩 설정(UTF-8)
        req.setCharacterEncoding("UTF-8");

        // 1) パラメータ収集（フォームのname属性と合わせる）
        // 1) 파라미터 수집 (폼의 name과 일치해야 함)
        String orderId   = req.getParameter("order_id");
        String productId = req.getParameter("product_id");
        String qtyStr    = req.getParameter("quantity");
        String dateStr   = req.getParameter("receive_date"); // 期待形式: yyyy-MM-dd / 기대 형식: yyyy-MM-dd
        String status    = req.getParameter("receive_status"); // 画面側で既定値「検収中」を設定 / 프론트에서 기본값 '검수중' 세팅
        String note      = req.getParameter("note");

        try {
            // 2) サービスへ委譲：入力検証＋登録（例外はサービス側で投げられる想定）
            // 2) 서비스 위임: 입력 검증 + 저장 (검증 실패 시 서비스에서 예외 발생)
            ReceiveInfo created = service.addReceiveInfo(orderId, productId, qtyStr, dateStr, status, note);

            // 3) 成功時：一覧画面へリダイレクト + トースト用メッセージをクエリに載せる
            // 3) 성공 시: 목록 화면으로 리다이렉트 + 토스트 메시지를 쿼리스트링으로 전달
            res.sendRedirect(req.getContextPath() + "/listReceiveInfos.do?msg=" +
                    java.net.URLEncoder.encode("登録に成功しました", "UTF-8")); // URLに日本語を含めるためエンコード / 한글/일본어 포함 시 URL 인코딩
            return null; // リダイレクト済みのためビューは不要 / 이미 리다이렉트했으므로 뷰 반환 없음

        } catch (IllegalArgumentException be) {
            // 入力検証エラー：フォームへ戻してエラーメッセージを表示
            // 입력 검증 실패: 폼으로 되돌리며 에러 메시지 표시
            req.setAttribute("errorMsg", be.getMessage());

            // フォーム選択肢などの再セット（プルダウン等がある場合）
            // 폼 선택 리스트 재세팅(드롭다운 등 사용 시)
            service.prepareFormLists(req);

            // ユーザーが入力した値をできるだけ復元（入力の手戻りを減らす）
            // 사용자가 입력한 값 복원(재입력 최소화)
            ReceiveInfo ri = new ReceiveInfo();
            ri.setOrder_id(orderId);
            ri.setProduct_id(productId);
            try { 
                ri.setQuantity(Integer.parseInt(qtyStr)); 
            } catch(Exception ignore){
                // 数値変換に失敗しても無視（フォーム側で再入力してもらう）
                // 숫자 변환 실패 시 무시(폼에서 다시 입력)
            }
            ri.setReceive_status(status);
            // receive_dateは文字列のままJSPのvalueへ渡すので、ここではDate変換しない
            // receive_date는 JSP에서 value로 그대로 쓰므로 여기서는 Date 변환 생략
            req.setAttribute("ri", ri);

            // 元の登録フォームを再表示
            // 기존 등록 폼으로 돌아감
            return "/WEB-INF/view/receiveInfoForm.jsp";

        } catch (Exception e) {
            // サーバ側の想定外エラー：一般的なメッセージを表示してフォームに戻す
            // 예기치 않은 서버 오류: 일반 메시지 노출 후 폼으로 복귀
            req.setAttribute("errorMsg", "サーバーエラーが発生しました。（登録失敗）");

            // フォーム選択肢の再セット
            // 폼 선택 리스트 재세팅
            service.prepareFormLists(req);

            // 再度フォーム表示
            // 폼 재표시
            return "/WEB-INF/view/receiveInfoForm.jsp";
        }
    }
}
