package com.purchase.command.supplierinfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

public class SearchSupplierInfoHandler implements CommandHandler {

    private final SupplierInfoService service = new SupplierInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // 중단 포함 체크 유지(검색 폼에서도 사용 가능)
            req.setAttribute("includeHidden", "1".equals(req.getParameter("includeHidden")));
            // 드롭다운 리스트 제공(폼/결과 테이블에서 재사용)
            req.setAttribute("supplierStatusList",
                    Arrays.asList("활성", "거래 중지", "폐업", "휴면"));
            return "/WEB-INF/view/supplierInfoSearch.jsp";
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            req.setCharacterEncoding("UTF-8");

            boolean includeHidden = "1".equals(req.getParameter("includeHidden"));
            String supplierName = trim(req.getParameter("supplier_name"));

            if (isEmpty(supplierName)) {
                req.setAttribute("message", "공급업체 이름을 입력하세요.");
                req.setAttribute("includeHidden", includeHidden);
                req.setAttribute("supplierStatusList",
                        Arrays.asList("활성", "거래 중지", "폐업", "휴면"));
                return "/WEB-INF/view/supplierInfoSearch.jsp";
            }

            // 검색
            List<SupplierInfo> list = service.searchSuppliersByName(supplierName);

            // 표시상태 필터: 기본은 A만, includeHidden=1이면 모두
            List<SupplierInfo> viewList = includeHidden
                    ? list
                    : list.stream()
                          .filter(s -> s.getRow_status() == null || "A".equalsIgnoreCase(s.getRow_status()))
                          .collect(Collectors.toList());

            if (viewList.isEmpty()) {
                req.setAttribute("message", "등록된 공급업체가 없습니다.");
            }

            // 결과/상태 바인딩
            req.setAttribute("supplier_name", supplierName);
            req.setAttribute("supplierList", viewList);
            req.setAttribute("includeHidden", includeHidden);
            req.setAttribute("supplierStatusList",
                    Arrays.asList("활성", "거래 중지", "폐업", "휴면"));

            return "/WEB-INF/view/supplierInfoSearch.jsp";
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
