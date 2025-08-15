package com.purchase.command.ReceiveInfo;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;
import mvc.command.CommandHandler;

public class ListReceiveInfoHandler implements CommandHandler {
    private final ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        List<ReceiveInfo> receiveList = service.getAllReceiveInfos();
        req.setAttribute("receiveList", receiveList);          // JSP에서 사용
        req.setAttribute("receiveStatusList", service.getReceiveStatusList());
        return "/WEB-INF/view/receiveInfoList.jsp";
    }
}
