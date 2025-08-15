package com.purchase.command.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;

import mvc.command.CommandHandler;

public class SearchProductHandler implements CommandHandler {

    private final ProductService productService = new ProductService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // ✅ 표시여부: 기본은 진행중(A)만, includeHidden=1이면 X도 포함
        boolean includeHidden = "1".equals(req.getParameter("includeHidden"));

        // 1) 파라미터 수집
        String productId   = req.getParameter("product_id");
        String productName = req.getParameter("product_name");
        String category    = req.getParameter("category");
        String supplierId  = req.getParameter("supplier_id");

        // 2) 조건 Map 구성(널/빈 제거)
        Map<String, String> searchParams = new HashMap<>();
        putIfNonEmpty(searchParams, "product_id", productId);
        putIfNonEmpty(searchParams, "product_name", productName);
        putIfNonEmpty(searchParams, "category", category);
        putIfNonEmpty(searchParams, "supplier_id", supplierId);

        // 3) 조회 (includeHidden 적용)
        List<Product> productList = productService.getProductsByConditions(searchParams, includeHidden);

        // 4) 바인딩 (JSP에서 사용하는 이름 유지)
        req.setAttribute("productList", productList);
        req.setAttribute("categoryList", productService.getCategoryList());
        req.setAttribute("productStatusList", productService.getProductStatusList()); // 업무상태 드롭다운
        req.setAttribute("includeHidden", includeHidden);                              // '중단 포함' 체크박스 상태
        req.setAttribute("selectedCategory", category);                                // 선택 유지

        // 5) 뷰 반환
        return "/WEB-INF/view/productList.jsp";
    }

    private static void putIfNonEmpty(Map<String,String> map, String key, String value) {
        if (value != null) {
            String v = value.trim();
            if (!v.isEmpty()) map.put(key, v);
        }
    }
}
