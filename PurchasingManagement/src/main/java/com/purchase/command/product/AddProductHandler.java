package com.purchase.command.product;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.dao.supplierinfo.SupplierInfoDao;
import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;
import com.purchase.vo.SupplierInfo;

import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class AddProductHandler implements CommandHandler {

    private static final String FORM = "/WEB-INF/view/AddProduct.jsp";

    private final ProductService productService = new ProductService();
    private final SupplierInfoDao supplierDao   = new SupplierInfoDao();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            injectDropdowns(req);
            return FORM;
        } else if ("POST".equalsIgnoreCase(req.getMethod())) {
            return handlePost(req, res);
        } else {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }

    private String handlePost(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String name     = t(req.getParameter("product_name"));
        String category = t(req.getParameter("category"));
        String priceStr = t(req.getParameter("price"));   // 자유 입력
        String supplier = t(req.getParameter("supplier_id"));
        String status   = t(req.getParameter("product_status"));

        Map<String,String> errors = new HashMap<>();
        if (name.isEmpty())     errors.put("product_name", "required");
        if (category.isEmpty()) errors.put("category", "required");
        if (supplier.isEmpty()) errors.put("supplier_id", "required");

        Integer price = null;
        if (priceStr.isEmpty()) {
            errors.put("price", "required");
        } else {
            // ★ 숫자만 추출 (쉼표/공백/통화기호 제거)
            String digits = priceStr.replaceAll("[^0-9]", "");
            if (digits.isEmpty()) {
                errors.put("price", "number");
            } else {
                try {
                    price = Integer.valueOf(digits);
                } catch (NumberFormatException e) {
                    errors.put("price", "number");
                }
            }
        }

        if (!errors.isEmpty()) {
            injectDropdowns(req);
            req.setAttribute("errors", errors);
            req.setAttribute("form_product_name", name);
            req.setAttribute("form_category", category);
            req.setAttribute("form_price", priceStr);
            req.setAttribute("form_supplier_id", supplier);
            req.setAttribute("form_product_status", status);
            return FORM;
        }

        Product p = new Product(null, name, category, price, supplier, status);
        productService.addProduct(p);

        injectDropdowns(req);
        req.setAttribute("success", true);
        return FORM;
    }

    private void injectDropdowns(HttpServletRequest req) throws Exception {
        try (Connection conn = ConnectionProvider.getConnection()) {
            List<SupplierInfo> supplierList = supplierDao.selectActiveSuppliers(conn);
            req.setAttribute("supplierList", supplierList);
        }
        req.setAttribute("categories", productService.getCategoryList());
        req.setAttribute("productStatusList", productService.getProductStatusList());
    }

    private String t(String s){ return s==null ? "" : s.trim(); }
}
