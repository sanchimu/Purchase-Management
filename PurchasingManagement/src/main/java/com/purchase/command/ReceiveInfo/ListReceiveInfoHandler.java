package com.purchase.command.ReceiveInfo;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;
import mvc.command.CommandHandler;

// 入庫情報一覧を取得して表示するハンドラー
public class ListReceiveInfoHandler implements CommandHandler {
    private final ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        // クエリパラメータ "mode" を取得
        // mode=avail → 返品可能なデータのみ表示
        // それ以外      → 全件表示
        String mode = req.getParameter("mode");
        boolean onlyAvailable = "avail".equalsIgnoreCase(mode); // trueなら返品可能のみ
        boolean includeHidden = true; // 非表示データを含むかどうか (必要に応じてfalseに変更可能)

        // サービスを利用して入庫情報リストをJOIN付きで一括取得
        List<ReceiveInfo> receiveList = service.getReceiveListJoin(onlyAvailable, includeHidden);

        // JSPで利用できるようにリクエスト属性にセット
        req.setAttribute("receiveList", receiveList);
        req.setAttribute("receiveStatusList", service.getReceiveStatusList());

        // 入庫一覧画面にフォワード
        return "/WEB-INF/view/receiveInfoList.jsp";
    }
}

