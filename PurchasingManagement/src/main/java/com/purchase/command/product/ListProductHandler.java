package com.purchase.command.product;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.dao.product.ProductDao;
import com.purchase.service.product.ProductService;
import com.purchase.vo.Product;

import mvc.command.CommandHandler;

public class ListProductHandler implements CommandHandler {

    private ProductService productService = new ProductService();
    private ProductDao productDao = new ProductDao();

    // ✅ 실무형 상품 상태 세트 (업무상태)
    // - 정상판매: 정상 주문 가능
    // - 일시품절: 재고 없음, 주문 불가/대기
    // - 예약판매: 선주문/리드타임 안내
    // - 판매보류: 품질/규제/이슈로 일시 판매 중단(재고와 무관)
    // - 단종: 생산/조달 종료(대체품 안내)
    private static final List<String> PRODUCT_STATUS =
            Arrays.asList("정상판매", "일시품절", "예약판매", "판매보류", "단종");

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // ✅ 표시여부: 기본은 진행중(A)만, includeHidden=1이면 X도 포함
        boolean includeHidden = "1".equals(req.getParameter("includeHidden"));

        // 기존 로직 유지
        List<Product> productList = productService.getAllProducts();
        List<String> categoryList = productService.getCategoryList();

        // ✅ row_status 필터링 (DAO 수정 없이 안전하게 메모리 필터)
        if (!includeHidden) {
            productList = productList.stream()
                .filter(p -> {
                    String rs = p.getRow_status(); // 'A' or 'X' (null이면 A 취급)
                    return rs == null || "A".equalsIgnoreCase(rs);
                })
                .collect(Collectors.toList());
        }

        // 뷰에서 사용할 바인딩
        req.setAttribute("productList", productList);
        req.setAttribute("categoryList", categoryList);
        req.setAttribute("productStatusList", PRODUCT_STATUS); // ✅ 새 상태 세트
        req.setAttribute("includeHidden", includeHidden);

        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/view/productList.jsp");
        rd.forward(req, res);
        return null;
    }
}
