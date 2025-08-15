package com.purchase.command.returnInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.returnInfo.ReturnInfoService;

import mvc.command.CommandHandler;

public class DeleteReturnInfoHandler implements CommandHandler {

    private final ReturnInfoService service = new ReturnInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        // 삭제는 반드시 POST로만 받기
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            res.sendRedirect(req.getContextPath() + "/returnInfoList.do");
            return null;
        }

        // ✅ JSP의 체크박스 name과 맞추세요: name="returnInfoIds"
        String[] ids = req.getParameterValues("returnInfoIds");

        if (ids == null || ids.length == 0) {
            // 메시지는 세션에 넣고 목록으로 리다이렉트
            req.getSession().setAttribute("errorMsg", "삭제할 항목을 선택하세요.");
            res.sendRedirect(req.getContextPath() + "/returnInfoList.do");
            return null;
        }

        service.deleteReturnInfo(ids);

        req.getSession().setAttribute("successMsg", "선택한 " + ids.length + "건을 삭제했습니다.");
        res.sendRedirect(req.getContextPath() + "/returnInfoList.do");
        return null;
    }
}
