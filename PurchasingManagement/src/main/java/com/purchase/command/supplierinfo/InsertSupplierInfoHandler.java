package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

// 공급업체를 추가할 때 사용되는 핸들러

public class InsertSupplierInfoHandler implements CommandHandler {

    private final SupplierInfoService supplierService = new SupplierInfoService();
    // 공급업체 정보를 처리하기 위한 서비스 클래스 객체 만들어 두는 것
    // 이 객체로 DB에 새로운 공급업체 등록 가능
    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
    	// 클라이언트의 요청이 왔을 때 실행. 

        if ("GET".equalsIgnoreCase(req.getMethod())) {
        	// 요청 방식이 GET 방식인지 확인
            return "/WEB-INF/view/supplierInfoForm.jsp";
            // GET 방식이면 등록 폼 화면을 보여주는 경로 반환
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
        	// 요청 방식이 POST 방식인지 확인
            req.setCharacterEncoding("UTF-8");

            String supplierId    = trim(req.getParameter("supplier_id"));
            String supplierName  = trim(req.getParameter("supplier_name"));
            String contactNumber = trim(req.getParameter("contact_number"));
            String address       = trim(req.getParameter("address"));
            // 폼에서 입력한 데이터를 지정한 문자에 저장. 앞뒤공백 제거

            if (isEmpty(supplierId) || isEmpty(supplierName)) {
            	// 공급업체 ID나 이름이 비어 있는지 확인
            	// 둘 중 하나라도 없으면 에러 처리
                req.setAttribute("error", "仕入先IDと名前は必須です。");
                // 에러 메세지를 JSP에 전달하기 위해 request에 "error"라는 이름으로 저장

                req.setAttribute("form_supplier_id", supplierId);
                req.setAttribute("form_supplier_name", supplierName);
                req.setAttribute("form_contact_number", contactNumber);
                req.setAttribute("form_address", address);
                // 사용자가 입력한 값을 다시 JSP로 보내줌

                return "/WEB-INF/view/supplierInfoForm.jsp";
                // 에러나면 등록폼으로 돌아감
            }

            SupplierInfo supplier = new SupplierInfo(supplierId, supplierName, contactNumber, address);
            // 입력받은 값들을 가지고 공급업체 정보를 담는 객체 생성. 이 객체를 이용해서 DB에 등록

            supplierService.insertSupplier(supplier);
            // 서비스 객체를 이용해서 공급업체 정보를 DB에 등록

            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null;
            // 등록이 끝나면 목록 페이지로 이동
        }

        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
        // GET이나 POST이외의 요청 방식이 들어왔을 때 "허용 안 됨" 이라는 응답을 보냄.
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
    // null 이 아닌 경우에는 앞뒤 공백 제거하고 반환, null 이면 그대로 null 반환

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
    // 문자열이 비었는지 확인하는 메서드. null 이거나 공백뿐이거나 빈 문자열이면 true 반환
}
