package com.purchase.command.supplierinfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

/**
 * 仕入先情報の検索を処理するハンドラー。
 * GET: 検索フォームの表示
 * POST: 検索実行・結果表示
 */
public class SearchSupplierInfoHandler implements CommandHandler {

    // サービス層のインスタンス
    private final SupplierInfoService service = new SupplierInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // GET 処理
            // 検索フォームを表示する
            // includeHidden のチェック状態を保持（フォームで利用可能）
            req.setAttribute("includeHidden", "1".equals(req.getParameter("includeHidden")));
            // 状態ドロップダウンリストを提供（フォーム/結果テーブルで再利用）
            req.setAttribute("supplierStatusList",
                    Arrays.asList("有効", "取引停止", "廃業", "休眠"));
            return "/WEB-INF/view/supplierInfoSearch.jsp";
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            // POST 処理
            // 文字エンコーディングを UTF-8 に設定
            req.setCharacterEncoding("UTF-8");

            boolean includeHidden = "1".equals(req.getParameter("includeHidden"));
            String supplierName = trim(req.getParameter("supplier_name"));

            // 入力チェック: 仕入先名が空ならエラーメッセージを表示
            if (isEmpty(supplierName)) {
                req.setAttribute("message", "仕入先名を入力してください。");
                req.setAttribute("includeHidden", includeHidden);
                req.setAttribute("supplierStatusList",
                        Arrays.asList("有効", "取引停止", "廃業", "休眠"));
                return "/WEB-INF/view/supplierInfoSearch.jsp";
            }

            // 検索処理
            // サービス経由で仕入先名による検索を実行
            List<SupplierInfo> list = service.searchSuppliersByName(supplierName);

            // 表示状態フィルタ
            // デフォルト: row_status = "A" または null のみ
            // includeHidden=1 の場合は全件
            List<SupplierInfo> viewList = includeHidden
                    ? list
                    : list.stream()
                          .filter(s -> s.getRow_status() == null || "A".equalsIgnoreCase(s.getRow_status()))
                          .collect(Collectors.toList());

            // 検索結果が空の場合のメッセージ
            if (viewList.isEmpty()) {
                req.setAttribute("message", "登録された仕入先はありません。");
            }

            // 検索結果・状態を JSP にバインド
            req.setAttribute("supplier_name", supplierName);
            req.setAttribute("supplierList", viewList);
            req.setAttribute("includeHidden", includeHidden);
            req.setAttribute("supplierStatusList",
                    Arrays.asList("有効", "取引停止", "廃業", "休眠"));

            return "/WEB-INF/view/supplierInfoSearch.jsp";
        }

        // 許可されていないメソッドの場合
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }

    // 文字列のトリム処理（null安全）
    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    // 空文字チェック（null または空白のみ）
    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
