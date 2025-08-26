package com.purchase.command.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.dto.OrderSheetDTO;
import com.purchase.service.order.OrderSheetService;

import mvc.command.CommandHandler;

public class SearchOrderSheetHandler implements CommandHandler {

    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 검색 파라미터 수집 / 検索パラメータ収集
        String orderId    = trim(req.getParameter("order_id"));
        String requestId  = trim(req.getParameter("request_id"));
        String supplierId = trim(req.getParameter("supplier_id"));
        String status     = trim(req.getParameter("order_status"));
        String fromDate   = trim(req.getParameter("from_date")); // 
        String toDate     = trim(req.getParameter("to_date"));   // 
        boolean includeHidden = "1".equals(req.getParameter("includeHidden"));

        // 화면에 입력값 유지 / 画面入力保持
        Map<String,String> searchParams = new HashMap<>();
        put(searchParams, "order_id", orderId);
        put(searchParams, "request_id", requestId);
        put(searchParams, "supplier_id", supplierId);
        put(searchParams, "order_status", status);
        put(searchParams, "from_date", fromDate);
        put(searchParams, "to_date", toDate);
        searchParams.put("includeHidden", includeHidden ? "1" : "0");
        req.setAttribute("searchParams", searchParams);

        // 검색 실행(서비스에 메서드 추가 예정) / 検索実行（サービス側に追加入予定）
        // ↓ 다음 단계에서 OrderSheetService.searchView(...) 를 추가하면 이 한 줄로 검색 동작함
        List<OrderSheetDTO> list = service.searchView(
                orderId, requestId, supplierId, status, fromDate, toDate, includeHidden
        );

        // 결과 바인딩 / 結果バインド
        req.setAttribute("orderList", list);
        req.setAttribute("includeHidden", includeHidden);
        req.setAttribute("orderStatusList", service.getOrderStatusList());

        // 기존 리스트 JSP 재사용 / 既存一覧JSP再利用
        return "/WEB-INF/view/orderSheetList.jsp";
    }

    private String trim(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    private void put(Map<String,String> m, String k, String v) {
        if (v != null && !v.isEmpty()) m.put(k, v);
    }
}
