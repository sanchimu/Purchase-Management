package com.purchase.command.ReceiveInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;

import mvc.command.CommandHandler;

// 入庫情報を追加するためのハンドラー (入庫登録処理)
public class AddReceiveInfoHandler implements CommandHandler {

    // サービスクラスのインスタンス
    private final ReceiveInfoService service = new ReceiveInfoService();

    // 入庫登録フォームのパス
    private static final String FORM = "/WEB-INF/view/receiveInfoForm.jsp";

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        // GETリクエスト → 入庫登録フォームを表示
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // ステータス(検収中/正常/入庫取消/返品処理など)のリストを取得してセット
            req.setAttribute("receiveStatusList", service.getReceiveStatusList());
            return FORM;
        }

        // POSTリクエスト → 入庫データを登録
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            req.setCharacterEncoding("UTF-8");

            String orderId        = req.getParameter("order_id");        // 注文ID
            String productId      = req.getParameter("product_id");      // 商品ID
            String quantityStr    = req.getParameter("quantity");        // 数量
            String receiveDateStr = req.getParameter("receive_date");    // 入庫日
            String statusParam    = req.getParameter("receive_status");  // 入庫ステータス(任意)

            // 数量を数値に変換（未入力の場合は0）
            int quantity = (quantityStr != null && !quantityStr.isEmpty())
                    ? Integer.parseInt(quantityStr) : 0;

            // VOオブジェクトにセット
            ReceiveInfo vo = new ReceiveInfo();
            vo.setOrder_id(orderId);
            vo.setProduct_id(productId);
            vo.setQuantity(quantity);

            // 入庫日が入力されている場合はDate型に変換してセット
            if (receiveDateStr != null && !receiveDateStr.isEmpty()) {
                java.util.Date parsedDate = null;
                try {
                    // 1) 日本語形式 (2025年08月07日) を試す
                    SimpleDateFormat sdfJP = new SimpleDateFormat("yyyy年MM月dd日");
                    parsedDate = sdfJP.parse(receiveDateStr);
                } catch (java.text.ParseException e1) {
                    try {
                        // 2) 標準形式 (2025-08-07) を試す
                        SimpleDateFormat sdfStd = new SimpleDateFormat("yyyy-MM-dd");
                        parsedDate = sdfStd.parse(receiveDateStr);
                    } catch (java.text.ParseException e2) {
                        throw new RuntimeException("対応できない日付形式です: " + receiveDateStr, e2);
                    }
                }
                vo.setReceive_date(parsedDate);
            }

            // 許可されたステータスのみ設定（それ以外はnull → DAO側で「検収中」をデフォルト適用）
            List<String> allowed = service.getReceiveStatusList();
            if (statusParam != null && allowed.contains(statusParam)) {
                vo.setReceive_status(statusParam);
            } else {
                vo.setReceive_status(null);
            }

            // 登録処理（DAOで新しい receive_id を設定する）
            service.addReceiveInfo(vo);

            // 登録完了後 → 入庫一覧画面へリダイレクト
            res.sendRedirect(req.getContextPath() + "/listReceiveInfos.do");
            return null;
        }

        // それ以外のHTTPメソッドの場合は405エラーを返す
        res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return null;
    }
}
