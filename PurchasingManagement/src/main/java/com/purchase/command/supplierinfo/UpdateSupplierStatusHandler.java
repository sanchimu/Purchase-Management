package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import com.purchase.service.supplierinfo.SupplierInfoService;
import mvc.command.CommandHandler;

// 공급업체 상태만 수정하는 핸들러 (리스트 화면 전용)
public class UpdateSupplierStatusHandler implements CommandHandler {

    private final SupplierInfoService supplierService = new SupplierInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("UTF-8");

        String[] ids = req.getParameterValues("supplier_id"); 
        // 리스트에서 체크된 공급업체 ID 배열

        if (ids == null || ids.length == 0) {
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null;
        }

        // 선택된 각 공급업체에 대해 상태만 업데이트
        for (String id : ids) {
            String newStatus = req.getParameter(id); // select name="supplier_id"
            if (newStatus == null) continue;
            supplierService.updateStatus(id, newStatus);
        }

        // alert 후 목록으로 이동
        res.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = res.getWriter()) {
            out.println("<script>alert('状態を更新しました。'); "
                      + "location.href='" + req.getContextPath() + "/listsupplier.do';</script>");
        }
        return null;
    }
}
