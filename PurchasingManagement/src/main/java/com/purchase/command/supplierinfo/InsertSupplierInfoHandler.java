package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

public class InsertSupplierInfoHandler implements CommandHandler {

    private final SupplierInfoService supplierService = new SupplierInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // 등록 폼으로 이동
            return "/WEB-INF/view/supplierInfoForm.jsp";
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            // 한글 파라미터 깨짐 방지
            req.setCharacterEncoding("UTF-8");

            // 파라미터 수집 & 기본 검증
            String supplierId    = trim(req.getParameter("supplier_id"));
            String supplierName  = trim(req.getParameter("supplier_name"));
            String contactNumber = trim(req.getParameter("contact_number"));
            String address       = trim(req.getParameter("address"));

            // 필수값 체크 (ID, 이름)
            if (isEmpty(supplierId) || isEmpty(supplierName)) {
                req.setAttribute("error", "공급업체 ID와 이름은 필수입니다.");
                // 사용자가 입력했던 값 유지
                req.setAttribute("form_supplier_id", supplierId);
                req.setAttribute("form_supplier_name", supplierName);
                req.setAttribute("form_contact_number", contactNumber);
                req.setAttribute("form_address", address);
                return "/WEB-INF/view/supplierInfoForm.jsp";
            }

            // VO 구성 (업무/표시상태는 DB 기본값 사용: supplier_status='활성', row_status='A')
            SupplierInfo supplier = new SupplierInfo(supplierId, supplierName, contactNumber, address);

            // 등록
            supplierService.insertSupplier(supplier);

            // 완료 후 목록으로
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null;
        }

        // 허용되지 않은 메서드
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
