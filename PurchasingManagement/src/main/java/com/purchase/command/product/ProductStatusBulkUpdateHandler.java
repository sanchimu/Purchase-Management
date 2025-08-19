package com.purchase.command.product;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.product.ProductService;

import mvc.command.CommandHandler;

public class ProductStatusBulkUpdateHandler implements CommandHandler {

    private final ProductService productService = new ProductService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            return redirectBack(req, res);
        }

        String[] ids = req.getParameterValues("ids");
        if (ids == null || ids.length == 0) {
            return redirectBack(req, res);
        }

        List<String> allowed = productService.getProductStatusList();
        Map<String,String> statusById = new LinkedHashMap<>();

        for (String id : new LinkedHashSet<>(Arrays.asList(ids))) {
            if (id == null) continue;
            String v = req.getParameter("status[" + id + "]");
            if (v == null) continue;
            v = v.trim();
            if (v.isEmpty()) continue;
            if (!allowed.contains(v)) continue;
            statusById.put(id, v);
        }

        if (!statusById.isEmpty()) {
            productService.updateProductStatusBulk(statusById);
        }

        return redirectBack(req, res);
    }

    private String redirectBack(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String referer = req.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            res.sendRedirect(referer);
        } else {
            String ih = req.getParameter("includeHidden");
            String qs = ("1".equals(ih) || "true".equalsIgnoreCase(ih)) ? "?includeHidden=1" : "";
            res.sendRedirect(req.getContextPath() + "/listProducts.do" + qs);
        }
        return null;
    }
}
