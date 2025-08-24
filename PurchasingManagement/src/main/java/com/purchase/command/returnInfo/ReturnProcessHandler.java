package com.purchase.command.returnInfo;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.purchase.service.ReceiveInfo.ReceiveInfoService;
import com.purchase.service.returnInfo.ReturnInfoService;
import com.purchase.vo.ReturnInfo;

import mvc.command.CommandHandler;

public class ReturnProcessHandler implements CommandHandler {

	private ReturnInfoService returnInfoService = new ReturnInfoService();

	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception { // 반품을 진행하는 핸들러
		// TODO Auto-generated method stub
		
		//우선 반품할 입고 정보에 표시할 정보와 기입할 정보에 해당하는 변수들을 선언
		// まず返品する入庫情報に表示する情報と入力する情報に対応する変数を宣言
		String receive_id= req.getParameter("receiveId");
		String product_id = returnInfoService.getProductIdByReceiveId(receive_id);
		String quantityStr = req.getParameter("quantity"); 
		String reason = req.getParameter("reason");
		Date return_date = new Date();
		
		if(receive_id == null || receive_id.length() == 0) { 	// 선택된 반품 항목이 없을 경우 
																// 選択された返品項目がない場合
			req.setAttribute("error",	"반품할 항목을 선택하세요");//에러 키값 갱신 (エラー用キー値を更新)
			return "/WEB-INF/view/returnInfoAdd.jsp";
		}else if (quantityStr == null || quantityStr.trim().isEmpty()) { 	//반품 이유를 적지 않았을 경우
																			// 返品理由が入力されていない場合
			req.setAttribute("error",	"반품 이유를 입력하세요"); // 에러 키값 갱신 (エラー用キー値を更新)
			return "/WEB-INF/view/returnInfoAdd.jsp";
		}
		
		int quantity;
		
		try {
			quantity = Integer.parseInt(quantityStr);	// String 형태로 서버에 전달했던 수량값을 다시 int형으로 형변환
														// サーバーにString型で渡された数量値をint型に変換
		}catch(NumberFormatException e) { 	// 변화 실패 시 (즉, 우린 수량을 글자로 적거나 공백이면 int 형으로 못바뀜
											// 変換失敗時（つまり数量を文字で入力した場合や空白の場合はint型に変換できない）
			req.setAttribute("error",	"정확한 반품 수량을 기재해주세요"); // 에러 키값 갱신 (エラー用キー値を更新)
			return "/WEB-INF/view/returnInfoAdd.jsp";
		}
		
		ReturnInfo returnInfo = new ReturnInfo(null, receive_id, product_id, quantity, reason, return_date); 	// 위 변수 값을 모아 객체선언
																												// 上記の変数値をまとめてReturnInfoオブジェクトを作成
		returnInfoService.addReturnInfo(returnInfo); 	//ReturnInfoService의 addReturnInfo 에 값 반환
														// ReturnInfoServiceのaddReturnInfoに値を渡す
		
		   req.setAttribute("success", true); // 성공 메시지용 값 반환 (成功メッセージ用の値を返す)

		    
		   res.sendRedirect(req.getContextPath() + "/returnInfoList.do"); 	// 그냥 경로 넣으면 핸들러를 안타서 데이터를 못불러옴, 이렇게 리다이렉트 형식으로 다시 해야 핸들러를탐
		   																	// 単にパスを入れるとハンドラーを通らずデータを取得できないため、リダイレクト形式で再度ハンドラーを通す
		   return null;
	}

}
