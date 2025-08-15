package com.purchase.command.returnInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import com.purchase.service.returnInfo.ReturnInfoService;

import mvc.command.CommandHandler;

public class UpdateReturnInfoHandler implements CommandHandler {

    private final ReturnInfoService service = new ReturnInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            res.sendRedirect(req.getContextPath() + "/returnInfoList.do");
            return null;
        }

        String[] ids = req.getParameterValues("returnInfoIds");
        if (ids == null || ids.length == 0) {
            req.getSession().setAttribute("errorMsg", "수정할 항목을 선택하세요.");
            res.sendRedirect(req.getContextPath() + "/returnInfoList.do");
            return null;
        }

        int success = 0;
        List<String> errors = new ArrayList<>();

        for (String id : ids) {
            String qtyStr   = req.getParameter("qty_" + id);     // 예: <input name="qty_RE001">
            String reason   = req.getParameter("reason_" + id);  // 예: <input name="reason_RE001">
            if (qtyStr == null || qtyStr.trim().isEmpty()) {
                errors.add(id + ": 수량이 비었습니다.");
                continue;
            }

            int qty;
            try { qty = Integer.parseInt(qtyStr.trim()); }
            catch (NumberFormatException nfe) {
                errors.add(id + ": 수량은 숫자여야 합니다.");
                continue;
            }

            try {
                service.updateReturnInfo(id, qty, reason);
                success++;
            } catch (IllegalArgumentException | IllegalStateException biz) {
                errors.add(id + ": " + biz.getMessage());
            } catch (RuntimeException ex) {
                errors.add(id + ": 저장 중 오류가 발생했습니다.");
            }
        }

        if (success > 0) {
            req.getSession().setAttribute("successMsg", "수정 완료: " + success + "건");
        }
        if (!errors.isEmpty()) {
            req.getSession().setAttribute("errorMsg", String.join("\\n", errors));
        }

        res.sendRedirect(req.getContextPath() + "/returnInfoList.do");
        return null;
    }
}
