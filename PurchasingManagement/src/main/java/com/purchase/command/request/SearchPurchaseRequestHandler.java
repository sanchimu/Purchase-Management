package com.purchase.command.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.request.PurchaseRequestService;
import com.purchase.vo.PurchaseRequest;

import mvc.command.CommandHandler;

public class SearchPurchaseRequestHandler implements CommandHandler {

    private final PurchaseRequestService service = new PurchaseRequestService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        boolean includeHidden = "1".equals(req.getParameter("includeHidden"));

        String requestId     = trim(req.getParameter("request_id"));
        String productId     = trim(req.getParameter("product_id"));
        String requesterName = trim(req.getParameter("requester_name"));

        // WHERE 조건을 담을 맵 
        // WHERE条件を格納するマップ
        Map<String,String> cond = new HashMap<>();
        
        //값이 있으면 조건에 추가 
        //値があれば条件に追加
        putIf(cond, "request_id", requestId);
        putIf(cond, "product_id", productId);
        putIf(cond, "requester_name", requesterName);

        // 조건 맵을 기반으로 서비스에서 검색 실행 
        // 条件マップに基づきサービスで検索を実行
        List<PurchaseRequest> list = service.getByConditions(cond);
        List<PurchaseRequest> view = includeHidden
                ? list
                : list.stream()
                      .filter(r -> r.getRow_status() == null || "A".equalsIgnoreCase(r.getRow_status()))
                      .collect(Collectors.toList());

        req.setAttribute("requestList", view);
        req.setAttribute("includeHidden", includeHidden);
        req.setAttribute("requestStatusList",
                Arrays.asList("접수","검토중","승인","반려","취소","종결"));

        return "/WEB-INF/view/purchaseRequestList.jsp";
    }

    private static String trim(String s){ return s==null?null:s.trim(); }
    private static void putIf(Map<String,String> map, String k, String v){
        if (v != null && !v.isEmpty()) map.put(k, v);
    }
}
