package com.purchase.command.order;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.order.OrderSheetService;
import com.purchase.vo.OrderSheet;

import mvc.command.CommandHandler;

public class ListOrderSheetHandler implements CommandHandler {

    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 표시상태 필터: includeHidden=1 이면 중단(X)도 보여주기
        boolean includeHidden = "1".equals(req.getParameter("includeHidden"));

        List<OrderSheet> all = service.getAll();
        List<OrderSheet> view = includeHidden
                ? all
                : all.stream()
                     .filter(o -> o.getRow_status() == null || "A".equalsIgnoreCase(o.getRow_status()))
                     .collect(Collectors.toList());

        // 화면 바인딩
        req.setAttribute("orderList", view);
        req.setAttribute("includeHidden", includeHidden);
        req.setAttribute("orderStatusList", service.getOrderStatusList()); // 요청/승인/발주/부분입고/완료/취소/보류

        // JSP 경로는 앞에 슬래시 필수!
        return "/WEB-INF/view/orderSheetList.jsp";
    }
}
