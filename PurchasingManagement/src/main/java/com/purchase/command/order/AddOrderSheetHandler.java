package com.purchase.command.order;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.dto.OrderSheetDTO;
import com.purchase.service.order.OrderSheetService;
import com.purchase.vo.OrderSheet;

import mvc.command.CommandHandler;

/**
 * 발주서 등록 핸들러 / 発注書登録ハンドラ
 *  GET  : 드롭다운 옵션 및 상태리스트 표시 / ドロップダウン・状態リスト表示
 * POST : 발주 등록 / 発注登録
 *
 * 상태값은 DB CHECK와 일치해야 함
 * （状態値はDBのCHECK制約と一致させること）
 */
public class AddOrderSheetHandler implements CommandHandler {

    private static final String FORM_VIEW = "/WEB-INF/view/AddOrderSheet.jsp";

    /** 기본 업무상태(일본 실무 표기) / 既定の業務状態（日本の表記） */
    private static final String DEFAULT_ORDER_STATUS = "発注依頼";

    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // 드롭다운 옵션 + 상태리스트 / ドロップダウン選択肢＋状態リスト
            List<OrderSheetDTO> options = service.getRequestOptions();
            req.setAttribute("requestOptions", options);
            req.setAttribute("orderStatusList", service.getOrderStatusList());
            return FORM_VIEW;
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            req.setCharacterEncoding("UTF-8");

            // ===== 파라미터 수집 / パラメータ収集 =====
            String requestId   = trim(req.getParameter("request_id"));
            String orderStatus = trim(req.getParameter("order_status"));

            // ===== 필수값 검증 / 必須チェック =====
            if (isEmpty(requestId)) {
                // 요청ID는 필수 / 申請IDは必須
                setFormAttrsWithError(req,
                        "요청ID는 필수입니다。/ 申請IDは必須です。",
                        orderStatus, requestId);
                return FORM_VIEW;
            }

            //  서버에서 supplier_id 재계산 / サーバ側で supplier_id 再計算 
            String supplierId = service.findSupplierIdByRequestId(requestId);
            if (supplierId == null) {
                setFormAttrsWithError(req,
                        "요청ID에 해당하는 공급업체를 찾을 수 없습니다。/ 申請IDに紐づく供給業者が見つかりません。",
                        orderStatus, requestId);
                return FORM_VIEW;
            }

            //  상태 기본값 및 유효성 검사 / 状態のデフォルト＆妥当性検証 
            if (isEmpty(orderStatus)) {
                orderStatus = DEFAULT_ORDER_STATUS; // "発注依頼"
            }
            List<String> allowed = service.getOrderStatusList(); // ["発注依頼","承認","発注","一部入荷","完了","取消","保留"]
            if (allowed != null && !allowed.isEmpty() && !allowed.contains(orderStatus)) {
                setFormAttrsWithError(req,
                        "유효하지 않은 상태값입니다（" + orderStatus + "）。/ 無効な状態値です。",
                        orderStatus, requestId);
                return FORM_VIEW;
            }

            //  신규 발주ID 생성 / 新規発注ID生成 
            String orderId = service.generateNextOrderId();

            //  VO 구성 / VO組み立て 
            OrderSheet os = new OrderSheet();
            os.setOrder_id(orderId);
            os.setRequest_id(requestId);
            os.setSupplier_id(supplierId);
            os.setOrder_date(new Date());
            os.setOrder_status(orderStatus);
            os.setRow_status("A"); // 기본 진행 / 既定は進行中

            try {
                //  등록 / 登録 
                service.insert(os);

                // 성공 → 목록으로 리다이렉트 / 成功→一覧へリダイレクト
                res.sendRedirect(req.getContextPath() + "/orderSheetList.do");
                return null;

            } catch (RuntimeException e) {
                // 흔한 ORA 에러를 사람이 읽기 쉬운 메시지로 변환 / 代表的なORAエラーを可読メッセージへ
                String msg = mapOracleError(e);
                setFormAttrsWithError(req, "등록 중 오류: " + msg + " / 登録中エラー: " + msg,
                        orderStatus, requestId);
                return FORM_VIEW;
            }
        }

        // 허용되지 않은 메서드 / 許可されていないメソッド
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }

    //  공용 폼 속성 세팅 / 共通フォーム属性設定 
    private void setFormAttrsWithError(HttpServletRequest req, String errorMessage,
                                       String orderStatus, String requestId) {
        req.setAttribute("error", errorMessage);
        req.setAttribute("requestOptions", service.getRequestOptions());
        req.setAttribute("orderStatusList", service.getOrderStatusList());

        // 선택 복원 / 選択の復元
        if (orderStatus != null) {
            req.setAttribute("selectedOrderStatus", orderStatus);
        }
        if (requestId != null) {
            req.setAttribute("selectedRequestId", requestId);
        }
    }

    //  ORA 에러 메시지 매핑 / ORAエラーのメッセージ変換 
    private String mapOracleError(RuntimeException e) {
        String m = String.valueOf(e.getMessage());

        // CHECK制約違反（状態値ミスマッチ）
        if (m.contains("ORA-02290")) {
            return "상태값이 테이블의 CHECK 제약을 위반했습니다(허용된 상태만 입력). / テーブルのCHECK制約に違反（許容された状態のみ可）";
        }
        // 文字列長オーバー
        if (m.contains("ORA-12899")) {
            return "입력값 길이가 컬럼 최대 길이를 초과했습니다. / 入力値がカラムの最大長を超過しました。";
        }
        // NOT NULLなど
        if (m.contains("ORA-01400") || m.contains("ORA-01407")) {
            return "필수 컬럼이 비어있습니다(널 금지). / 必須カラムがNULLです（NULL禁止）。";
        }
        return m; // 기타는 원문 유지 / その他は原文
    }

    //  유틸 / ユーティリティ 
    private static String trim(String s){ return s==null?null:s.trim(); }
    private static boolean isEmpty(String s){ return s==null || s.trim().isEmpty(); }
}
