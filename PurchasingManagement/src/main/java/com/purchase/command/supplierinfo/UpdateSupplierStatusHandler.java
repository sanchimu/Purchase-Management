package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import com.purchase.service.supplierinfo.SupplierInfoService;
import mvc.command.CommandHandler;

// 공급업체 상태만 수정하는 핸들러 (리스트 화면 전용)
// 仕入先の状態のみを修正するハンドラー（一覧画面専用）
public class UpdateSupplierStatusHandler implements CommandHandler {

    private final SupplierInfoService supplierService = new SupplierInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("UTF-8");

        String[] ids = req.getParameterValues("supplier_id"); 
        // 리스트에서 체크된 공급업체 ID 배열
        // 一覧でチェックされた仕入先IDの配列

        if (ids == null || ids.length == 0) {
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null;
        }
        // 선택된 ID가 없으면 목록 페이지로 리다이렉트
        // 選択されたIDがなければ一覧ページへリダイレクト

        // 선택된 각 공급업체에 대해 상태만 업데이트
        // 選択された各仕入先に対して状態のみを更新
        for (String id : ids) {
            String newStatus = req.getParameter(id); // select name="supplier_id"
            // select 태그의 name이 supplier_id로 되어 있으므로 ID를 키로 상태값 가져옴
            // selectタグのnameが supplier_id なので、IDをキーに状態値を取得

            if (newStatus == null) continue;
            supplierService.updateStatus(id, newStatus);
        }

        // alert 후 목록으로 이동
        // アラートを出した後、一覧へ移動
        res.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = res.getWriter()) {
            out.println("<script>alert('状態を更新しました。'); "
                      + "location.href='" + req.getContextPath() + "/listsupplier.do';</script>");
        }
        return null;
    }
}
