package com.purchase.command.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;

import mvc.command.CommandHandler;

public class ListProductHandler implements CommandHandler {

    private static final String VIEW = "/WEB-INF/view/productList.jsp";
    private final ProductService productService = new ProductService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // includeHidden: "1"/"true" → 포함
        String ih = n(req.getParameter("includeHidden"));
        boolean includeHidden = "1".equals(ih) || "true".equalsIgnoreCase(ih);

        // 검색 조건 수집
        Map<String,String> cond = new HashMap<>();
        put(cond, "product_id",   req.getParameter("product_id"));
        put(cond, "product_name", req.getParameter("product_name"));
        put(cond, "category",     req.getParameter("category"));
        put(cond, "supplier_id",  req.getParameter("supplier_id"));

        // DB에서 조건 + 표시여부 반영하여 조회
        List<Product> productList  = productService.getProductsByConditions(cond, includeHidden);
        List<String>  categoryList = productService.getCategoryList();

        // JSP 바인딩
        req.setAttribute("productList", productList);
        req.setAttribute("categoryList", categoryList);
        req.setAttribute("productStatusList", productService.getProductStatusList()); // 드롭다운
        req.setAttribute("includeHidden", includeHidden ? "1" : "0");                 // 문자열로 통일

        RequestDispatcher rd = req.getRequestDispatcher(VIEW);
        rd.forward(req, res);
        return null;
    }

    private void put(Map<String,String> m, String key, String val){
        if (val != null && !val.trim().isEmpty()) m.put(key, val.trim());
    }
    private String n(String s){ return s==null? "" : s.trim(); }
}
