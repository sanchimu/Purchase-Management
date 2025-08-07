package com.purchase.command.supplierinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

public class ListSupplierInfoHandler implements CommandHandler {
	// CommandHandler 인터페이스의 process()메서드를 구현해야 함

    private SupplierInfoService supplierService = new SupplierInfoService();
    // SupplierInfoService 클래스의 객체를 사용해서 공급업체 목록을 가져오는 역할을 수행
    // Controller는 직접 DB에 접근하지 않고 Service를 통해서만 데이터를 가져옴

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        List<SupplierInfo> supplierList = supplierService.getSupplierList();
        // supplierService의 getSupplierList()메서드 호출.
        // DB의 공급업체 정보 읽어와서 SupplierInfo객체들이 들어있는 List 형태로 반환해옴
        req.setAttribute("supplierList", supplierList);
        // supplierList를 JSP에 전달하기 위해 request 객체에 저장. 
        // "supplierList" : jsp에서 꺼내 쓸 때 이름

        return "/WEB-INF/view/supplierInfoList.jsp";
        // 요청 처리가 끝난 뒤 보여줄 경로 반환
    }
}
