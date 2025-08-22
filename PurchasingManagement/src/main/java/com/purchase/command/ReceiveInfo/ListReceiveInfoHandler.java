package com.purchase.command.ReceiveInfo;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.vo.ReceiveInfo;
import mvc.command.CommandHandler;

// 入庫情報一覧を取得して表示するハンドラー
// 입고 정보 목록을 조회하여 표시하는 핸들러
public class ListReceiveInfoHandler implements CommandHandler {
    
    // サービスクラスのインスタンス生成
    // 서비스 클래스 인스턴스 생성
    private final ReceiveInfoService service = new ReceiveInfoService();

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

        // クエリパラメータ "mode" を取得
        // 쿼리 파라미터 "mode" 값 가져오기
        // mode=avail → 返品可能なデータのみ表示
        //              반품 가능한 데이터만 표시
        // それ以外    → 全件表示
        //              그 외는 전체 표시
        String mode = req.getParameter("mode");

        // "avail" なら true → 반품 가능 데이터만 조회
        boolean onlyAvailable = "avail".equalsIgnoreCase(mode);

        // 非表示データを含むかどうか (true=含む)
        // 숨김 데이터를 포함할지 여부 (true=포함)
        // ※ 必要に応じて false に変更可能
        boolean includeHidden = true;

        // サービスを利用して入庫情報リストをJOIN付きで一括取得
        // 서비스 호출 → JOIN 포함 입고 목록 일괄 조회
        List<ReceiveInfo> receiveList = service.getReceiveListJoin(onlyAvailable, includeHidden);

        // JSPで利用できるようにリクエスト属性にセット
        // JSP에서 사용 가능하도록 request 속성에 저장
        req.setAttribute("receiveList", receiveList);
        req.setAttribute("receiveStatusList", service.getReceiveStatusList());

        // 入庫一覧画面にフォワード
        // 입고 목록 화면으로 forward (JSP 렌더링)
        return "/WEB-INF/view/receiveInfoList.jsp";
    }
}
