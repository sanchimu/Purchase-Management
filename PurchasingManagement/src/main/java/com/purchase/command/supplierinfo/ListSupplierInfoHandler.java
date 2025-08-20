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
 * 仕入先情報一覧を表示するハンドラークラス。
 * SupplierInfoService を利用してデータを取得し、
 * 条件に応じてフィルタリングを行った上で JSP に渡す。
 */
public class ListSupplierInfoHandler implements CommandHandler {

    // サービス層のインスタンスを生成
    private final SupplierInfoService supplierService = new SupplierInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // パラメータ取得
        // includeHidden=1 の場合は非表示（中断状態を含む）データも表示
        // デフォルトでは false（= "A" のみ表示）
        boolean includeHidden = "1".equals(req.getParameter("includeHidden"));

        // 全件取得
        // SupplierInfoService 経由で全仕入先データを取得
        List<SupplierInfo> all = supplierService.getSupplierList();

        // フィルタリング処理
        // デフォルト: row_status が "A" または null のデータのみを表示
        // includeHidden=1 が指定された場合はフィルタリングせず全件表示
        List<SupplierInfo> viewList = includeHidden
                ? all
                : all.stream()
                     .filter(s -> s.getRow_status() == null || "A".equalsIgnoreCase(s.getRow_status()))
                     .collect(Collectors.toList());

        // リクエスト属性にバインド
        // JSP 側で一覧表示に利用するデータをセット
        req.setAttribute("supplierList", viewList);
        req.setAttribute("includeHidden", includeHidden);

        // 業務状態ドロップダウン用リスト
        // JSP 側でセレクトボックスを生成するための選択肢を提供
        req.setAttribute("supplierStatusList",
                Arrays.asList("有効", "取引停止", "廃業", "休眠"));

        // 遷移先 JSP
        // 仕入先一覧を表示する JSP のパスを返す
        return "/WEB-INF/view/supplierInfoList.jsp";
    }
}
