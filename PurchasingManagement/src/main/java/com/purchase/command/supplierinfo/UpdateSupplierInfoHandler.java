package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

// 공급업체 정보 수정하는 핸들러. DB에 업데이트 

public class UpdateSupplierInfoHandler implements CommandHandler {

    private final SupplierInfoService supplierService = new SupplierInfoService();
    // DB에 데이터를 수정하기 위한 서비스 객체 생성

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
    	// 웹 요청이 들어왔을 때 실행되는 메서드
        req.setCharacterEncoding("UTF-8");

        String supplierId     = req.getParameter("supplier_id");
        String supplierName   = req.getParameter("supplier_name");
        String contactNumber  = req.getParameter("contact_number");
        String address        = req.getParameter("address");
        String supplierStatus = req.getParameter("supplier_status");
        String rowStatus      = req.getParameter("row_status");
        // 사용자가 폼에 입력한 값을 request에서 거내옴. 

        if (supplierId == null || supplierId.isEmpty()) {
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null;
        }
        // 공급업체 아이디가 비어있으면 목록 페이지로 리다이렉트

        SupplierInfo vo = new SupplierInfo();
        vo.setSupplier_id(supplierId);
        vo.setSupplier_name(supplierName);
        vo.setContact_number(contactNumber);
        vo.setAddress(address);
        vo.setSupplier_status(supplierStatus);
        vo.setRow_status((rowStatus == null || rowStatus.isEmpty()) ? "A" : rowStatus);
        // 공급업체 VO에 값 담음
        

        supplierService.updateSupplier(vo);
        // 서비스 클래스를 통해 DB에 업데이트 실행

        // alert 출력용
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/html; charset=UTF-8");
        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        res.setHeader("Pragma", "no-cache");

        String next = req.getContextPath() + "/listsupplier.do";
        // 이동할 다음 URL을 변수에 저장
        try (PrintWriter out = res.getWriter()) {
        	// 브라우저에게 직접 HTML 응답 출력
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body>");
            out.println("<script>");
            out.println("alert('修正が完了しました。');");
            out.println("location.href='" + next + "';");
            out.println("</script>");
            out.println("</body></html>");
            out.flush();
        }
        return null;
    }
}
