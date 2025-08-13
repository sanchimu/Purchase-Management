package com.purchase.command.supplierinfo;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

public class SearchSupplierInfoHandler implements CommandHandler {

    private final SupplierInfoService service = new SupplierInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        if (req.getMethod().equalsIgnoreCase("GET")) {
            // GET → 검색 폼 표시
            return "/WEB-INF/view/supplierInfoSearch.jsp";

        } else if (req.getMethod().equalsIgnoreCase("POST")) {
            // POST → 검색 실행
            String supplierName = req.getParameter("supplier_name");

            if (supplierName == null || supplierName.trim().isEmpty()) {
                // 검색어 없음 → 메시지 + 폼 유지
                req.setAttribute("message", "공급업체 이름을 입력하세요.");
                return "/WEB-INF/view/supplierInfoSearch.jsp";
            }

            List<SupplierInfo> list = service.searchSuppliersByName(supplierName.trim());

            if (list.isEmpty()) {
                req.setAttribute("message", "등록된 공급업체가 없습니다.");
                return "/WEB-INF/view/supplierInfoSearch.jsp";
            }

            // 결과 전달
            req.setAttribute("supplier_name", supplierName);
            req.setAttribute("supplierList", list);

            return "/WEB-INF/view/supplierInfoSearch.jsp";

        } else {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }
}
