package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;

import mvc.command.CommandHandler;

public class DeleteSupplierInfoHandler implements CommandHandler {
    // CommandHandler 인터페이스의 process()메서드를 구현해야 함

    private SupplierInfoService supplierService = new SupplierInfoService();
    // 공급업체 삭제 로직을 실행할 서비스 객체 생성

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 요청을 처리하고 결과 페이지 경로나 null 반환
        if (req.getMethod().equalsIgnoreCase("POST")) {
            // 요청 방식이 POST인지 확인
            String[] supplierIds = req.getParameterValues("supplier_id");
            // 체크박스에서 선택된 모든 supplier_id 값을 배열로 받음

            if (supplierIds != null && supplierIds.length > 0) {
                for (String id : supplierIds) {
                    if (id != null && !id.trim().isEmpty()) {
                        supplierService.deleteSupplier(id);
                    }
                }
            }

            // 삭제 후 목록 페이지로
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null; // JSP 경로 없음 → 리다이렉트
        } else {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }
}
