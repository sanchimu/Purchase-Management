package com.purchase.command.ReceiveInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.service.order.OrderSheetService;
import com.purchase.service.product.ProductService;
import com.purchase.vo.ReceiveInfo;

import mvc.command.CommandHandler;

// 入庫情報を追加するためのハンドラー (入庫登録処理)
// 입고 정보를 추가하기 위한 핸들러 (입고 등록 처리)
public class AddReceiveInfoHandler implements CommandHandler {

    // サービスクラスのインスタンス / 서비스 클래스 인스턴스
    private final ReceiveInfoService service = new ReceiveInfoService();

    // 入庫登録フォームのパス / 입고 등록 폼 경로
    private static final String FORM = "/WEB-INF/view/receiveInfoForm.jsp";

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        // === GETリクエストの場合 ===
        // GET 요청 → 입고 등록 폼을 표시
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // ステータスリストをセット (検収中/正常/入庫取消/返品処理など)
            // 상태 리스트 설정 (검수중/정상/입고취소/반품처리 등)
            req.setAttribute("receiveStatusList", service.getReceiveStatusList());

            // 商品一覧をセット（ドロップダウン用）
            // 상품 목록 설정 (드롭다운용)
            req.setAttribute("productList", new ProductService().getAllProducts());

            // 注文一覧をセット（ドロップダウン用）
            // 주문 목록 설정 (드롭다운용)
            req.setAttribute("orderList", new OrderSheetService().getRequestOptions());

            return FORM; // JSP 폼 페이지로 이동
        }

        // === POSTリクエストの場合 ===
        // POST 요청 → 입고 데이터 등록
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            req.setCharacterEncoding("UTF-8");

            // フォームからパラメータ取得 / 폼에서 파라미터 값 가져오기
            String orderId        = req.getParameter("order_id");        // 注文ID / 주문ID
            String productId      = req.getParameter("product_id");      // 商品ID / 상품ID
            String quantityStr    = req.getParameter("quantity");        // 数量 / 수량
            String receiveDateStr = req.getParameter("receive_date");    // 入庫日 / 입고일
            String statusParam    = req.getParameter("receive_status");  // 入庫ステータス / 입고 상태

            // 数量を数値に変換（未入力の場合は0）
            // 수량을 숫자로 변환 (미입력 시 0)
            int quantity = (quantityStr != null && !quantityStr.isEmpty())
                    ? Integer.parseInt(quantityStr) : 0;

            // VOオブジェクトにセット / VO 객체에 값 세팅
            ReceiveInfo vo = new ReceiveInfo();
            vo.setOrder_id(orderId);
            vo.setProduct_id(productId);
            vo.setQuantity(quantity);

            // 入庫日をDate型に変換してセット
            // 입고일을 Date 타입으로 변환 후 세팅
            if (receiveDateStr != null && !receiveDateStr.isEmpty()) {
                java.util.Date parsedDate = null;
                try {
                    // 1) 日本語形式 (例: 2025年08月07日) を試す
                    // 일본식 날짜 포맷 시도
                    SimpleDateFormat sdfJP = new SimpleDateFormat("yyyy年MM月dd日");
                    parsedDate = sdfJP.parse(receiveDateStr);
                } catch (java.text.ParseException e1) {
                    try {
                        // 2) 標準形式 (例: 2025-08-07) を試す
                        // 표준 날짜 포맷 시도
                        SimpleDateFormat sdfStd = new SimpleDateFormat("yyyy-MM-dd");
                        parsedDate = sdfStd.parse(receiveDateStr);
                    } catch (java.text.ParseException e2) {
                        // 対応できない形式の場合エラー / 처리 불가능한 경우 예외 발생
                        throw new RuntimeException("対応できない日付形式です: " + receiveDateStr, e2);
                    }
                }
                vo.setReceive_date(parsedDate);
            }

            // ステータスは許可されたもののみセット
            // 상태는 허용된 값만 세팅
            List<String> allowed = service.getReceiveStatusList();
            if (statusParam != null && allowed.contains(statusParam)) {
                vo.setReceive_status(statusParam);
            } else {
                // DAO側でデフォルト「検収中」を適用
                // DAO 쪽에서 기본값 "검수중" 적용
                vo.setReceive_status(null);
            }

            // 登録処理（DAOで新しい receive_id を採番）
            // 등록 처리 (DAO에서 새 receive_id 채번)
            service.addReceiveInfo(vo);

            // 登録完了後、入庫一覧画面へリダイレクト
            // 등록 완료 후, 입고 목록 화면으로 리다이렉트
            res.sendRedirect(req.getContextPath() + "/listReceiveInfos.do");
            return null;
        }

        // その他のHTTPメソッド → 405エラー
        // 그 외의 HTTP 메소드 → 405 에러 반환
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }
}
