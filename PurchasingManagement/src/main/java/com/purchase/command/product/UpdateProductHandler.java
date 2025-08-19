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

public class UpdateProductHandler implements CommandHandler {

    private static final String FORM = "/WEB-INF/view/updateProduct.jsp";

    private final ProductService productService = new ProductService();
    private final SupplierInfoDao supplierDao   = new SupplierInfoDao();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            String id = req.getParameter("product_id");
            if (id == null || id.trim().isEmpty()) {
                res.sendRedirect(req.getContextPath() + "/listProducts.do");
                return null;
            }
            Product p = productService.findById(id);
            if (p == null) {
                res.sendRedirect(req.getContextPath() + "/listProducts.do");
                return null;
            }
            injectDropdowns(req);
            req.setAttribute("p", p);
            req.setAttribute("includeHidden", nz(req.getParameter("includeHidden")));
            return FORM;

        } else if ("POST".equalsIgnoreCase(req.getMethod())) {
            return handlePost(req, res);
        } else {
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return null;
        }
    }

    private String handlePost(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String id       = tz(req.getParameter("product_id"));
        String name     = tz(req.getParameter("product_name"));
        String category = tz(req.getParameter("category"));   // ★ 카테고리 수정 허용
        String priceStr = tz(req.getParameter("price"));      // 자유입력 → 서버에서 숫자 추출
        String supplier = tz(req.getParameter("supplier_id"));
        String status   = tz(req.getParameter("product_status"));

        Map<String,String> errors = new HashMap<>();
        if (id.isEmpty())       errors.put("product_id", "required");
        if (name.isEmpty())     errors.put("product_name", "required");
        if (category.isEmpty()) errors.put("category", "required");
        if (supplier.isEmpty()) errors.put("supplier_id", "required");
        if (priceStr.isEmpty()) errors.put("price", "required");

        Integer price = null;
        if (!priceStr.isEmpty()) {
            try {
                String digits = priceStr.replaceAll("[^0-9]", "");
                price = digits.isEmpty() ? null : Integer.valueOf(digits);
                if (price == null) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                errors.put("price", "number");
            }
        }

        if (!errors.isEmpty()) {
            injectDropdowns(req);
            Product p = new Product(id, name, category, price == null ? 0 : price, supplier, status, "A");
            req.setAttribute("p", p);
            req.setAttribute("errors", errors);
            req.setAttribute("includeHidden", nz(req.getParameter("includeHidden")));
            return FORM;
        }

        Product p = new Product();
        p.setProduct_id(id);
        p.setProduct_name(name);
        p.setCategory(category);
        p.setPrice(price);
        p.setSupplier_id(supplier);
        p.setProduct_status(status);

        productService.update(p);

        String ih = req.getParameter("includeHidden");
        String qs = ("1".equals(ih) || "true".equalsIgnoreCase(ih)) ? "?includeHidden=1" : "";
        res.sendRedirect(req.getContextPath() + "/listProducts.do" + qs);
        return null;
    }

    private void injectDropdowns(HttpServletRequest req) throws Exception {
        try (Connection conn = ConnectionProvider.getConnection()) {
            List<SupplierInfo> supplierList = supplierDao.selectActiveSuppliers(conn);
            req.setAttribute("supplierList", supplierList);
        }
        req.setAttribute("categories", productService.getCategoryList());
        req.setAttribute("productStatusList", productService.getProductStatusList());
    }

    private String tz(String s){ return s==null? "" : s.trim(); }
    private String nz(String s){ return s==null? "" : s.trim(); }
}
