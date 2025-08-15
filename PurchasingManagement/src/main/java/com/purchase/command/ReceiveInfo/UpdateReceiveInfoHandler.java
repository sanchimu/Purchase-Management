package com.purchase.command.ReceiveInfo;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import mvc.command.CommandHandler;

public class UpdateReceiveInfoHandler implements CommandHandler {
    private final ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }

        String[] ids = req.getParameterValues("receiveIds");
        List<String> statuses = new ArrayList<>();
        if (ids != null) {
            for (String id : ids) {
                statuses.add(req.getParameter("status_" + id)); // JSP의 name="status_${r.receive_id}"
            }
            service.updateStatuses(ids, statuses);
        }

        res.sendRedirect(req.getContextPath() + "/listReceiveInfos.do");
        return null;
    }
}
