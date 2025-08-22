package com.purchase.command.supplierinfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

// 공급업체 목록 화면을 처리하는 핸들러 클래스
public class ListSupplierInfoHandler implements CommandHandler {

    private final SupplierInfoService supplierService = new SupplierInfoService();
    // DB에서 공급업체 데이터 가져오기 위한 서비스 객체 생성

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
    	// 웹 요청이 들어왔을 때 실행되는 메서드

        boolean includeHidden = "1".equals(req.getParameter("includeHidden"));
        // URL 파라미터 중 "includeHidden=1"이 있으면 true, 없으면 false
        // 중단된 공급업체도 보여줄지 여부를 판단하는 것

        String supplierName = req.getParameter("supplier_name");
        // 검색 파라미터: 공급업체 이름 (공백 또는 null일 수 있음)

        List<SupplierInfo> all = supplierService.getSupplierList();
        // DB에서 모든 공급업체 정보 가져와서 all 이라는 리스트에 저장

        List<SupplierInfo> viewList = all.stream()
            .filter(s -> includeHidden || s.getRow_status() == null || "A".equalsIgnoreCase(s.getRow_status()))
            // includeHidden == true 면 모든 항목 허용, 아니면 A 상태 또는 null만 허용
            .filter(s -> supplierName == null || supplierName.trim().isEmpty()
                      || s.getSupplier_name().contains(supplierName.trim()))
            // supplier_name 파라미터가 있는 경우, 이름에 해당 문자열이 포함되는 항목만 필터링
            .collect(Collectors.toList());

        req.setAttribute("supplierList", viewList);
        // 목록에 표시할 공급업체 데이터를 JSP 에 전달

        req.setAttribute("includeHidden", includeHidden);
        // 체크박스를 유지할 수 있게 JSP에 넘겨줌

        req.setAttribute("supplierStatusList",
                Arrays.asList("有効", "取引停止", "廃業", "休眠"));
        // 상태 선택용 드롭다운 리스트를 JSP에 넘겨줌

        return "/WEB-INF/view/supplierInfoList.jsp";
    }
}
