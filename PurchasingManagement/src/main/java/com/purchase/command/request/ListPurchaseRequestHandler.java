package com.purchase.command.request;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.request.PurchaseRequestService;
import com.purchase.vo.PurchaseRequest;

import mvc.command.CommandHandler;

public class ListPurchaseRequestHandler implements CommandHandler {

    private final PurchaseRequestService service = new PurchaseRequestService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        boolean includeHidden = "1".equals(req.getParameter("includeHidden"));

        // product와 JOIN된 전체 목록을 서비스에서 조회 
        // productとJOINされた全件一覧をサービスで照会
        List<PurchaseRequest> all = service.getJoinedRequestList();
        List<PurchaseRequest> view = includeHidden
                ? all
                : all.stream()
                     .filter(r -> r.getRow_status() == null || "A".equalsIgnoreCase(r.getRow_status()))
                     .collect(Collectors.toList());

        req.setAttribute("requestList", view);
        req.setAttribute("includeHidden", includeHidden);
        req.setAttribute("requestStatusList",
                Arrays.asList("受付","検討中","承認","差し戻し","取り消し","完了"));

        return "/WEB-INF/view/purchaseRequestList.jsp";
    }
}
