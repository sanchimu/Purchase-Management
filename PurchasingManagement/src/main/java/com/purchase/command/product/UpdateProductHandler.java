package com.purchase.command.product;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;

import mvc.command.CommandHandler;

public class UpdateProductHandler implements CommandHandler {

    private final ProductService productService = new ProductService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 선택된 상품 ID 들 (체크박스 name="ids")
        String[] ids = req.getParameterValues("ids");
        if (ids == null || ids.length == 0) {
            return redirectBack(req, res);
        }

        // 허용되는 업무상태 화이트리스트
        List<String> allowed = productService.getProductStatusList(); // ["정상판매","일시품절","예약판매","판매보류","단종"]

        // 중복 제거 후 업데이트
        Arrays.stream(ids).distinct().forEach(id -> {
            // 각 행의 select name이 상품ID이므로 그대로 읽음
            String newStatus = req.getParameter(id);
            if (newStatus == null) return;            // 파라미터 없으면 스킵
            if (!allowed.contains(newStatus)) return; // 허용 외 값 방어

            Product p = new Product();
            p.setProduct_id(id);
            p.setProduct_status(newStatus);
            productService.updateProductStatus(p);
        });

        return redirectBack(req, res);
    }

    private String redirectBack(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String referer = req.getHeader("Referer");
        res.sendRedirect(referer != null ? referer : (req.getContextPath() + "/listProducts.do"));
        return null;
    }
}
