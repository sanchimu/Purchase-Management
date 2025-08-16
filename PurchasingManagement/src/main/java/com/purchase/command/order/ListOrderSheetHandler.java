package com.purchase.command.order;

import com.purchase.service.order.OrderSheetService;
import com.purchase.dto.OrderSheetDTO;
import mvc.command.CommandHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ListOrderSheetHandler implements CommandHandler {

    private final OrderSheetService service = new OrderSheetService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        boolean includeHidden = "1".equals(req.getParameter("includeHidden"));
        List<OrderSheetDTO> list = service.getAllView(includeHidden);

        req.setAttribute("orderList", list);
        req.setAttribute("includeHidden", includeHidden);
        req.setAttribute("orderStatusList", service.getOrderStatusList());

        return "/WEB-INF/view/orderSheetList.jsp";
    }
}
