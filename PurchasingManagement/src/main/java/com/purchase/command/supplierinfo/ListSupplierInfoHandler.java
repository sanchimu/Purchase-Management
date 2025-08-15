package com.purchase.command.supplierinfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

public class ListSupplierInfoHandler implements CommandHandler {

    private final SupplierInfoService supplierService = new SupplierInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 중단 포함 여부: 기본 false (A만)
        boolean includeHidden = "1".equals(req.getParameter("includeHidden"));

        // 전체 목록 조회 (서비스/DAO는 그대로 사용)
        List<SupplierInfo> all = supplierService.getSupplierList();

        // 기본은 row_status = 'A' 만 표시, includeHidden=1이면 필터없이 모두
        List<SupplierInfo> viewList = includeHidden
                ? all
                : all.stream()
                     .filter(s -> s.getRow_status() == null || "A".equalsIgnoreCase(s.getRow_status()))
                     .collect(Collectors.toList());

        // JSP 바인딩
        req.setAttribute("supplierList", viewList);
        req.setAttribute("includeHidden", includeHidden);

        // 업무상태 드롭다운(활성/거래 중지/폐업/휴면) – JSP에서 없으면 만들어 쓰지만 여기서도 제공
        req.setAttribute("supplierStatusList",
                Arrays.asList("활성", "거래 중지", "폐업", "휴면"));

        return "/WEB-INF/view/supplierInfoList.jsp";
    }
}
