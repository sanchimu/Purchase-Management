package com.purchase.command.returnInfo;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.returnInfo.ReturnInfoService;
import com.purchase.vo.ReturnInfo;

import mvc.command.CommandHandler;

public class ListReturnInfoHandler implements CommandHandler {

    private final ReturnInfoService service = new ReturnInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 서비스에서 전체 목록 조회
        List<ReturnInfo> list = service.getAllReturnInfo(); // getAll() 사용해도 됨

        // JSP에서 기대하는 이름으로 바인딩 (returnList)
        req.setAttribute("returnInfoList", list);

        // JSP 경로는 소문자 파일명으로 통일
        return "/WEB-INF/view/returnInfoList.jsp";
    }
}
