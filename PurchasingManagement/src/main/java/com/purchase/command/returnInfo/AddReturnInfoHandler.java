package com.purchase.command.returnInfo;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;

import mvc.command.CommandHandler;

public class AddReturnInfoHandler implements CommandHandler {

    private final ReceiveInfoService receiveInfoService = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        // 이 핸들러는 "반품 등록 화면"만 보여줍니다. (등록 처리 = /returnProcess.do)
        if ("GET".equalsIgnoreCase(req.getMethod())) {

            // 반품 가능 수량(available_to_return) > 0 인 입고건 목록 조회
            List<ReceiveInfo> receiveInfoList = receiveInfoService.getReceiveInfoWithReturnQty();

            // JSP에서 사용할 이름으로 바인딩 (JSP에서 ${receiveInfoList}로 사용)
            req.setAttribute("receiveInfoList", receiveInfoList);

            // JSP 경로 — 대소문자 섞이면 404 날 수 있으니 소문자 파일명을 추천
            // 기존 파일명이 ReturnInfoAdd.jsp 라면 그대로 두세요.
            return "/WEB-INF/view/returnInfoAdd.jsp";
        }

        // 그 외 메서드는 허용하지 않음 (등록 처리는 /returnProcess.do가 담당)
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }
}
