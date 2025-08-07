package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

public class InsertSupplierInfoHandler implements CommandHandler {
	// CommandHandler 인터페이스의 process()메서드를 구현해야 함

    private SupplierInfoService supplierService = new SupplierInfoService();
    // 공급업체 등록 로직을 실행할 서비스 객체 생성
    // SupplierInfoService 안에 addSupplier() 같은 메서드들 있음

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        if (req.getMethod().equalsIgnoreCase("GET")) {
        	// 요청 방식이 GET 인지 확인
           
            return "/WEB-INF/view/supplierInfoForm.jsp";
            // GET 요청이면 공급업체 등록 폼 페이지 경로를 반환
            // WEB-INF 폴더 안의 JSP 는 직접 URL 로 접근 불가. Controller를 거쳐야 함
        } else if (req.getMethod().equalsIgnoreCase("POST")) {
        	// POST 요청이면 실제 데이터 등록 작업 수행
           
        	// 폼에서 전송된 입력값을 요청 객체에서 꺼냄
            String supplierId = req.getParameter("supplier_id");
            String supplierName = req.getParameter("supplier_name");
            String contactNumber = req.getParameter("contact_number");
            String address = req.getParameter("address");

            SupplierInfo supplier = new SupplierInfo(supplierId, supplierName, contactNumber, address);
            // 입력값을 하나의 SupplierInfo 객체로 묶음
            
            supplierService.insertSupplier(supplier);
            // Service에 등록 요청

            // 등록 후 목록페이지로
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null;
        } else {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }
}
