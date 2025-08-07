package com.purchase.command.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.order.OrderSheetService;
import com.purchase.vo.OrderSheet;

import mvc.command.CommandHandler;

public class ListOrderSheetHandler implements CommandHandler {
	
	 private OrderSheetService service = new OrderSheetService();

	    @Override
	    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
	        List<OrderSheet> list = service.getAll();
	        req.setAttribute("orderList", list);
	        return "WEB-INF/view/orderSheetList.jsp";
	    }
	}
