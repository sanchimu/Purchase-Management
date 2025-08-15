package com.purchase.command.supplierinfo;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

public class UpdateSupplierInfoHandler implements CommandHandler {

    private final SupplierInfoService supplierService = new SupplierInfoService();

    // 허용 상태 화이트리스트
    private static final List<String> ALLOWED =
            Arrays.asList("활성","거래 중지","폐업","휴면");

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("UTF-8");

        String[] ids = req.getParameterValues("supplier_id"); // 체크된 항목
        if (ids == null || ids.length == 0) {
            return redirectBack(req, res);
        }

        Arrays.stream(ids).distinct().forEach(id -> {
            String newStatus = req.getParameter(id); // select name=공급업체ID
            if (newStatus == null || !ALLOWED.contains(newStatus)) return;

            SupplierInfo s = new SupplierInfo();
            s.setSupplier_id(id);
            s.setSupplier_status(newStatus);
            supplierService.updateSupplierStatus(s);
        });

        return redirectBack(req, res);
    }

    private String redirectBack(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String referer = req.getHeader("Referer");
        res.sendRedirect(referer != null ? referer : (req.getContextPath() + "/listsupplier.do"));
        return null;
    }
}
