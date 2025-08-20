package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

/**
 * 仕入先を新規登録するためのハンドラークラス。
 * - GETリクエスト: 登録フォーム画面を表示する。
 * - POSTリクエスト: フォームから送信されたデータを受け取り、仕入先をDBに登録する。
 * - その他メソッド: 許可しない（405エラーを返す）。
 */
public class InsertSupplierInfoHandler implements CommandHandler {

    // Serviceクラスのインスタンスを保持。実際のDB操作はService→DAOに委譲。
    private final SupplierInfoService supplierService = new SupplierInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        // GETリクエスト処理
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // 「/insertsupplier.do」がGETで呼ばれた場合は、
            // 新規登録フォーム（supplierInfoForm.jsp）を表示するだけ。
            // データベース処理は行わない。
            return "/WEB-INF/view/supplierInfoForm.jsp";
        }

      
        // POSTリクエスト処理
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            // 日本語などのマルチバイト文字が文字化けしないように文字コードをUTF-8に設定。
            req.setCharacterEncoding("UTF-8");

           
            // フォームから送信されたパラメータを取得
            // trim()をかけて前後の空白を除去（安全対策）。
            String supplierId    = trim(req.getParameter("supplier_id"));      // 仕入先ID
            String supplierName  = trim(req.getParameter("supplier_name"));    // 仕入先名
            String contactNumber = trim(req.getParameter("contact_number"));   // 連絡先
            String address       = trim(req.getParameter("address"));          // 住所

       
            // 必須チェック (IDと名前は必須項目)
            if (isEmpty(supplierId) || isEmpty(supplierName)) {
                // エラーメッセージをリクエスト属性に格納してJSPに渡す
                req.setAttribute("error", "仕入先IDと名前は必須です。");

                // 入力内容を再度フォームに保持させるため、リクエスト属性にセット
                // これをしないとエラー時に入力値が消えてしまう。
                req.setAttribute("form_supplier_id", supplierId);
                req.setAttribute("form_supplier_name", supplierName);
                req.setAttribute("form_contact_number", contactNumber);
                req.setAttribute("form_address", address);

                // 再び登録フォームJSPにフォワード
                return "/WEB-INF/view/supplierInfoForm.jsp";
            }

       
            // VO(Value Object)の生成
            // SupplierInfo VOを作成し、フォームの値を格納。
            // 業務状態(supplier_status)と表示状態(row_status)はDB側のデフォルト値を利用するため、ここでは指定しない。
            // DBのデフォルト: supplier_status='有効', row_status='A'
            SupplierInfo supplier = new SupplierInfo(supplierId, supplierName, contactNumber, address);

            // 登録処理の実行
            // Serviceを呼び出し、DAOを経由してDBにINSERTする。
            supplierService.insertSupplier(supplier);


            // 正常終了後のリダイレクト

            // 登録が成功したら仕入先一覧ページへリダイレクトする。
            // sendRedirectを使うことでブラウザのURLも更新される。
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");

            // 処理が終わったのでnullを返す（JSPフォワードはしない）
            return null;
        }


        // その他のHTTPメソッド (PUT, DELETEなど)

        // このハンドラーはGETとPOSTのみを想定。
        // それ以外のメソッドが呼ばれた場合は「405 Method Not Allowed」を返す。
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }


    // ユーティリティメソッド群


    /**
     * nullチェック付きtrim。
     * 文字列がnullならnullを返し、nullでなければ前後の空白を除去して返す。
     */
    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    /**
     * 空文字チェック。
     * nullまたは空文字、または空白だけの場合はtrueを返す。
     */
    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
