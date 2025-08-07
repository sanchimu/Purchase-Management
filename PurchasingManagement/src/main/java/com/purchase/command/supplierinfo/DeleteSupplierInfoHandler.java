package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;

import mvc.command.CommandHandler;

public class DeleteSupplierInfoHandler implements CommandHandler {
	// CommandHandler 인터페이스의 process()메서드를 구현해야 함

    private SupplierInfoService supplierService = new SupplierInfoService();
    // 공급업체 삭제 로직을 실행할 서비스 객체 생성
    // SupplierInfoService 안에 removeSupplier() 같은 메서드들 있음

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
    // 요청을 처리하고 결과 페이지 경로나 null 반환
        if (req.getMethod().equalsIgnoreCase("POST")) {
        	// 요청 방식이 POST인지 확인
            String supplierId = req.getParameter("supplier_id");
            // 파라미터 중 supplier_id 값을 꺼냄

            if (supplierId != null && !supplierId.trim().isEmpty()) {
                supplierService.deleteSupplier(supplierId);
            }
            // supplierId가 null이 아니고 빈 문자열이 아닐 때 삭제 

            // 삭제 후 목록페이지로
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            // sendRedirect() -> 클라이언트에게 다른 주소로 다시 요청하도록 지시
            // req.getContextPath() 현재 프로젝트의 기본 경로 자동 반환
            return null; // jsp 경로가 없으므로 지금 메서드에서 더 이상 화면이동x
        } else {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            // 요청 방식이 POST가 아닐 경우 응답 상태 코드를 Method Not Allowed로 설정
            return null;
        }
    }
}
