package com.purchase.command.supplierinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.supplierinfo.SupplierInfoService;
import com.purchase.vo.SupplierInfo;

import mvc.command.CommandHandler;

// 공급업체 화면에서 특정 항목 클릭했을 때, 해당 공급업체의 상세/수정 페이지로 이동하는 핸들러
// 주어진 supplied_id 로 DB에서 공급업체 조회, JSP에서 사용할 데이터를 request에 담아 편집 화면으로 포워딩
// 仕入先画面で特定の項目をクリックした際、その仕入先の詳細/編集ページに移動するハンドラー
// 指定された supplier_id でDBから仕入先を検索し、JSPで使用するデータをリクエストに格納して編集画面にフォワードする

public class EditSupplierInfoHandler implements CommandHandler {

    private final SupplierInfoService supplierService = new SupplierInfoService();
    // 공급업체 정보를 가져오기 위한 서비스 객체 생성. 
    // 이 객체를 이용해서 DB에 있는 공급업체 정보 불러오기가 가능
    // 仕入先情報を取得するためのサービスオブジェクトを生成。
    // このオブジェクトを利用してDB内の仕入先情報を取得することが可能

    @Override
    public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
    	// 웹 요청이 들어오면 호출되는 핸들러 메서드
        // Webリクエストが入ってきたときに呼び出されるハンドラーメソッド

        req.setCharacterEncoding("UTF-8");
        // 요청 들어온 데이터의 문자 인코딩을 UTF-8로 지정
        // リクエストで受け取ったデータの文字エンコーディングをUTF-8に指定

        String supplierId = req.getParameter("supplier_id");
        // 클라이언트의 요청에서 supplier_id 라는 이름의 파라미터 값 꺼내옴. 
        // 어떤 공급업체를 수정할 것인지 식별하는 역할
        // クライアントのリクエストから「supplier_id」という名前のパラメータ値を取得。
        // どの仕入先を編集するかを識別する役割

        if (supplierId == null || supplierId.isEmpty()) {
        	// supplier_id가 없거나 비어있다면
            // supplier_id が存在しない、または空の場合

            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null;
            // 공급업체 목록 페이지로 강제 이동, 더 이상 이 메서드는 실행되지 않음.
            // 仕入先一覧ページへリダイレクトし、このメソッドの処理は終了
        }

        SupplierInfo supplier = supplierService.getSupplierById(supplierId);
        // supplier_id로 DB에서 해당 공급업체의 상세정보 가져와서 SupplierInfo로 반환
        // supplier_id を使ってDBから該当する仕入先の詳細情報を取得し、SupplierInfo として返す

        if (supplier == null) {
            res.sendRedirect(req.getContextPath() + "/listsupplier.do");
            return null;
        }
        // 해당 ID의 공급업체가 DB에 없으면 목록페이지로 강제이동
        // 指定されたIDの仕入先がDBに存在しない場合は一覧ページへリダイレクト

        req.setAttribute("supplierStatusList", new String[]{"有効", "取引停止", "廃業", "休眠"});
        // JSP 에서 공급업체 상태 선택할 수 있게 하기 위해 상태목록 배열을 request에 저장
        // JSPで仕入先の状態を選択できるように、状態リストの配列をリクエストに格納

        req.setAttribute("supplier", supplier);
        // DB에서 조회한 SupplierInfo객체로 JSP에서 사용할수 있게 request에 저장
        // DBから取得した SupplierInfo オブジェクトをJSPで利用できるようにリクエストに格納

        return "/WEB-INF/view/supplierInfoEdit.jsp";
        // 수정 페이지 JSP 경로 반환
        // 編集ページのJSPパスを返す
    }
}
