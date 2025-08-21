package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

public class UpdateSupplierInfoHandler implements CommandHandler {

    private final SupplierInfoService supplierService = new SupplierInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("UTF-8");

        String supplierId     = req.getParameter("supplier_id");
        String supplierName   = req.getParameter("supplier_name");
        String contactNumber  = req.getParameter("contact_number");
        String address        = req.getParameter("address");
        String supplierStatus = req.getParameter("supplier_status"); // 有効/取引停止/廃業/休眠
        String rowStatus      = req.getParameter("row_status");      // A/X

        if (supplierId == null || supplierId.isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null;
        }

        SupplierInfo vo = new SupplierInfo();
        vo.setSupplier_id(supplierId);
        vo.setSupplier_name(supplierName);
        vo.setContact_number(contactNumber);
        vo.setAddress(address);
        vo.setSupplier_status(supplierStatus);
        vo.setRow_status((rowStatus == null || rowStatus.isEmpty()) ? "A" : rowStatus);

        supplierService.updateSupplier(vo);

        // ✅ 별도 페이지 없이 즉시 alert + 목록으로 이동
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/html; charset=UTF-8");
        // 캐시 방지(선택)
        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        res.setHeader("Pragma", "no-cache");

        String next = req.getContextPath() + "/listsupplier.do";
        try (PrintWriter out = res.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body>");
            out.println("<script>");
            out.println("alert('修正が完了しました。');"); // = 수정완료되었습니다
            out.println("location.href='" + next + "';");
            out.println("</script>");
            out.println("</body></html>");
            out.flush();
        }
        return null;
    }
}
