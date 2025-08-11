package com.purchase.controller;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/receiveInfoList.do")
public class ListReceiveInfoServlet extends HttpServlet {

    private ReceiveInfoService service;

    @Override
    public void init() throws ServletException {
        service = new ReceiveInfoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ReceiveInfo> receiveList = service.getAllReceiveInfos();

        // null 체크 대신 빈 리스트도 처리 가능
        if (receiveList == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "입고 정보 조회 실패");
            return;
        }

        request.setAttribute("receiveInfoList", receiveList);
        request.getRequestDispatcher("/WEB-INF/view/receiveInfoList.jsp").forward(request, response);
    }
}
