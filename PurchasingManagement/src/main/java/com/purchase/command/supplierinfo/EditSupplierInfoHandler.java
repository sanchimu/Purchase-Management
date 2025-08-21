package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

public class EditSupplierInfoHandler implements CommandHandler {

    private final SupplierInfoService supplierService = new SupplierInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("UTF-8");

        String supplierId = req.getParameter("supplier_id");
        if (supplierId == null || supplierId.isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null;
        }

        SupplierInfo supplier = supplierService.getSupplierById(supplierId);
        if (supplier == null) {
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null;
        }

        // 한국어 상태 셀렉트박스(없으면 JSP에서 기본값 만들어 쓰지만 여기서 제공)
        req.setAttribute("supplierStatusList", new String[]{"有効", "取引停止", "廃業", "休眠"});
        req.setAttribute("supplier", supplier);

        return "/WEB-INF/view/supplierInfoEdit.jsp";
    }
}
