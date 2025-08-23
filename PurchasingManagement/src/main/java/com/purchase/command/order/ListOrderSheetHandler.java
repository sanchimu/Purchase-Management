package com.purchase.command.order;

import com.purchase.service.order.OrderSheetService;
import com.purchase.dto.OrderSheetDTO;
import mvc.command.CommandHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 발주서 목록 핸들러 / 発注書一覧ハンドラ
 * 
 *  includeHidden=1 → 중단(X) 포함 조회 / includeHidden=1 → 停止(X)も含めて取得
 *  Service 계층에서 DTO(List<OrderSheetDTO>)를 받아 JSP에 전달
 *  orderStatusList: JSP 드롭다운用 상태 리스트 / JSPのドロップダウン用ステータスリスト
 */
public class ListOrderSheetHandler implements CommandHandler {

    // Service 계층 / サービス層
    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // "중단 포함" 체크 여부 / 「停止含む」チェック有無
        boolean includeHidden = "1".equals(req.getParameter("includeHidden"));

        // 발주서 목록 조회 / 発注書一覧取得
        List<OrderSheetDTO> list = service.getAllView(includeHidden);

        // JSP 전달용 속성 세팅 / JSPへ渡す属性を設定
        req.setAttribute("orderList", list);                       // 목록 / 一覧
        req.setAttribute("includeHidden", includeHidden);          // 중단 포함 여부 / 停止含むフラグ
        req.setAttribute("orderStatusList", service.getOrderStatusList()); // 상태목록 / ステータス一覧

        // 결과 JSP 경로 반환 / 結果JSPのパスを返す
        return "/WEB-INF/view/orderSheetList.jsp";
    }
}
