package com.purchase.controller;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/receiveInfoList.do") // ✅ URL 매핑 추가 (.do 확장자 사용)
public class ListReceiveInfoServlet extends HttpServlet {

    private ReceiveInfoService service;

    @Override
    public void init() throws ServletException {
        service = new ReceiveInfoService(); // super.init() 제거해도 무방
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ReceiveInfo> receiveList = service.getAllReceiveInfos();

        if (receiveList == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "입고 정보 조회 실패");
            return;
        }

        request.setAttribute("receiveList", receiveList);
        request.getRequestDispatcher("/WEB-INF/view/receiveInfoList.jsp").forward(request, response);
    }
}

