package com.purchase.command.request;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.request.PurchaseRequestService;

import mvc.command.CommandHandler;

public class UpdatePurchaseRequestHandler implements CommandHandler {

    private final PurchaseRequestService service = new PurchaseRequestService();

    // ✅ 실무형 상태 세트
    private static final List<String> ALLOWED =
            Arrays.asList("受付","検討中","承認","差し戻し","取り消し","完了");

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("UTF-8");

        String[] ids = req.getParameterValues("ids"); // 체크된 요청ID
        if (ids == null || ids.length == 0) {
            return redirectBack(req, res);
        }

        Map<String,String> idToStatus = new LinkedHashMap<>();
        Arrays.stream(ids).distinct().forEach(id -> {
            String newStatus = req.getParameter(id); // select name=요청ID
            if (newStatus != null && ALLOWED.contains(newStatus)) {
                idToStatus.put(id, newStatus);
            }
        });

        if (!idToStatus.isEmpty()) {
            service.updateRequestStatusBulk(idToStatus);
        }

        return redirectBack(req, res);
    }

    private String redirectBack(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String referer = req.getHeader("Referer");
        res.sendRedirect(referer != null ? referer : (req.getContextPath() + "/requestList.do"));
        return null;
    }
}
