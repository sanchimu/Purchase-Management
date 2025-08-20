package com.purchase.command.supplierinfo;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

/**
 * 仕入先の状態（有効、取引停止、廃業、休眠）を更新するためのハンドラー。
 * - チェックボックスで選択された仕入先IDに対してのみ状態を変更。
 * - 不正な状態値は無視する。
 */
public class UpdateSupplierInfoHandler implements CommandHandler {

    // サービスクラス。実際のDB更新処理はService → DAOに委譲。
    private final SupplierInfoService supplierService = new SupplierInfoService();

    // 許可される状態のホワイトリスト（このリスト以外は更新不可）
    private static final List<String> ALLOWED =
            Arrays.asList("有効","取引停止","廃業","休眠");

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 日本語入力の文字化け防止（マルチバイト文字対応）
        req.setCharacterEncoding("UTF-8");

  
        // 1. チェックされた仕入先IDを取得

        String[] ids = req.getParameterValues("supplier_id"); // チェックボックスの値
        if (ids == null || ids.length == 0) {
            // 1件も選択されていない場合は元の画面へリダイレクト
            return redirectBack(req, res);
        }


        // 2. 選択されたIDごとに処理

        Arrays.stream(ids).distinct().forEach(id -> {
            // selectタグのnameが仕入先IDになっているので、それで新しい状態を取得
            String newStatus = req.getParameter(id);

            // 新しい状態がnull または ホワイトリストにない場合は無視
            if (newStatus == null || !ALLOWED.contains(newStatus)) return;

            // VOを生成して更新対象データを格納
            SupplierInfo s = new SupplierInfo();
            s.setSupplier_id(id);          // 主キー（仕入先ID）
            s.setSupplier_status(newStatus); // 更新する状態

            // サービスを通じてDB更新
            supplierService.updateSupplierStatus(s);
        });


        // 3. 完了後リダイレクト（元のページに戻す）
        return redirectBack(req, res);
    }

    /**
     * 元の画面に戻す処理
     * - Refererヘッダーがあればそこにリダイレクト
     * - なければ仕入先一覧ページに戻る
     */
    private String redirectBack(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String referer = req.getHeader("Referer");
        res.sendRedirect(referer != null ? referer : (req.getContextPath() + "/listsupplier.do"));
        return null;
    }
}
