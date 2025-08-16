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
        // 쿼리파라미터로 모드 제어 (옵션)
        // ?mode=all  -> 전체
        // ?mode=avail -> 반품가능만
        String mode = req.getParameter("mode");
        boolean onlyAvailable = "avail".equalsIgnoreCase(mode); // 기본: 전체가 아니라면 필요 시 false로
        boolean includeHidden = true; // 숨김 포함여부(필요시 false로)

        // 조인 버전 한방 조회
        List<ReceiveInfo> receiveList = service.getReceiveListJoin(onlyAvailable, includeHidden);

        req.setAttribute("receiveList", receiveList);
        req.setAttribute("receiveStatusList", service.getReceiveStatusList());
        return "/WEB-INF/view/receiveInfoList.jsp";
    }
}
