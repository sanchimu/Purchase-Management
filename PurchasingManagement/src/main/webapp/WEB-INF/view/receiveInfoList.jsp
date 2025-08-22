<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> <%-- JSP 기본설정 / JSP基本設定 --%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %> <%-- JSTL コアタグ / JSTL 코어 태그 --%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- JSTL フォーマット(日付・数値) / 날짜·숫자 포맷 --%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %> <%-- JSTL 関数タグ / 문자열 함수 --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/> <%-- コンテキストパスを保持 / 컨텍스트 경로 저장 --%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>入庫リスト</title> <%-- ページタイトル: 入庫リスト / 페이지 제목: 입고 목록 --%>

<style>
  /* ページ共通の簡易スタイル / 페이지 공통 간단 스타일 */
  body {
    font-family: "Noto Sans JP", "Meiryo", "Yu Gothic", "Hiragino Kaku Gothic ProN", sans-serif;
    margin: 20px;
  }
  h2{margin-bottom:10px;}
  .toolbar{display:flex;gap:8px;align-items:center;margin:10px 0 16px;flex-wrap:wrap;}
  .toolbar form{display:flex;gap:8px;align-items:center;}
  input[type="text"], input[type="date"]{padding:6px 8px;}
  input[type="submit"],a.btn,button.btn{padding:6px 10px;border:1px solid #ccc;background:#f7f7f7;text-decoration:none;color:#333;cursor:pointer;}
  table{border-collapse:collapse;width:100%;}
  th,td{border:1px solid #ddd;padding:8px;text-align:center;}
  th{background:#f2f2f2;}
  .right{text-align:right;}
  .muted{color:#666;}
</style>

<!-- flatpickr: カレンダー入力UI / 캘린더 입력 UI -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/ja.js"></script>

<script>
  // ===== チェックボックス操作 / 체크박스 조작 =====

  // 一括選択/解除（ヘッダのチェックで全行に反映）
  // 전체 선택/해제(헤더 체크가 모든 행에 반영)
  function toggleAll(source){
    const boxes=document.querySelectorAll('input[name="receiveIds"]');
    boxes.forEach(cb=>cb.checked=source.checked);
  }

  // 何も選択されていない場合は送信しない
  // 아무 항목도 선택되지 않으면 전송 금지
  function ensureChecked(){
    const checked=document.querySelectorAll('input[name="receiveIds"]:checked');
    if(checked.length===0){
      alert('1つ以上の項目を選択してください。'); // 항목을 1개 이상 선택하세요
      return false;
    }
    return confirm('選択した項目の業務状態を保存しますか？'); // 선택한 항목의 상태를 저장하시겠습니까?
  }

  // ===== flatpickr 初期化 / flatpickr 초기화 =====
  document.addEventListener("DOMContentLoaded", function() {
    flatpickr("input[name='fromDate']", {
      locale: "ja",
      altInput: true,               // 表示用の別入力 / 표시용 별도 입력
      altFormat: "Y年m月d日",       // 画面表示形式 / 화면 표시 형식
      dateFormat: "Y-m-d"           // サーバ送信形式 / 서버 전송 형식
    });
    flatpickr("input[name='toDate']", {
      locale: "ja",
      altInput: true,
      altFormat: "Y年m月d日",
      dateFormat: "Y-m-d"
    });
  });
</script>
</head>
<body>

<h2>入庫リスト</h2> <%-- 入庫データの一覧表示 / 입고 데이터 목록 표시 --%>

<div class="toolbar">
  <!-- 新規入庫登録ボタン / 신규 입고 등록 버튼 -->
  <!-- c:url は自動でコンテキストパス付与 / c:url은 자동으로 컨텍스트 경로를 붙임 -->
  <a class="btn" href="<c:url value='/addReceiveInfo.do'/>">入庫登録</a>

  <!-- 簡易検索フォーム（商品名/仕入先/期間） / 간단 검색 폼(상품명/공급업체/기간) -->
  <form action="<c:url value='/listReceiveInfos.do'/>" method="get">
    <input type="text" name="productName" value="${fn:escapeXml(param.productName)}" placeholder="商品名"/> <%-- 商品名検索 / 상품명 검색 --%>
    <input type="text" name="supplierName" value="${fn:escapeXml(param.supplierName)}" placeholder="仕入先"/> <%-- 仕入先検索 / 공급업체 검색 --%>
    <input type="text" name="fromDate" value="${param.fromDate}" placeholder="開始日"/> <%-- 期間開始 / 기간 시작 --%>
    ~
    <input type="text" name="toDate" value="${param.toDate}" placeholder="終了日"/> <%-- 期間終了 / 기간 종료 --%>
    <input type="submit" class="btn" value="検索"/> <%-- 検索ボタン / 검색 버튼 --%>
  </form>
</div>

<!-- 一括更新フォーム（選択行の業務状態を保存） / 일괄 업데이트 폼(선택 행의 업무 상태 저장) -->
<form action="<c:url value='/updateReceiveInfos.do'/>" method="post" onsubmit="return ensureChecked();">
  <table>
    <tr>
      <th><input type="checkbox" onclick="toggleAll(this)"></th> <%-- 全選択チェック / 전체 선택 체크박스 --%>
      <th>入庫ID</th>   <%-- 入庫ID / 입고ID --%>
      <th>注文ID</th>   <%-- 注文ID / 주문ID --%>
      <th>商品ID</th>   <%-- 商品ID / 상품ID --%>
      <th>商品名</th>   <%-- 商品名 / 상품명 --%>
      <th>仕入先</th>   <%-- 仕入先 / 공급업체 --%>
      <th class="right">数量</th> <%-- 数量 / 수량 --%>
      <th>入庫日</th>   <%-- 入庫日 / 입고일 --%>
      <th class="right">返品可能</th> <%-- 返品可能数 / 반품 가능 수량 --%>
      <th>業務状態</th> <%-- 業務状態 / 업무 상태 --%>
    </tr>

    <c:choose>
      <c:when test="${empty receiveList}">
        <tr><td colspan="10">該当する入庫情報がありません。</td></tr> <%-- データなしメッセージ / 데이터 없음 안내 --%>
      </c:when>
      <c:otherwise>
        <%-- 明細行レンダリング / 상세 행 렌더링 --%>
        <c:forEach var="r" items="${receiveList}">
          <tr>
            <td><input type="checkbox" name="receiveIds" value="${r.receive_id}"></td> <%-- 選択チェック / 선택 체크 --%>
            <td>${r.receive_id}</td>
            <td>${r.order_id}</td>
            <td>${r.product_id}</td>
            <td>${empty r.product_name ? '-' : r.product_name}</td>
            <td>${empty r.supplier_name ? '-' : r.supplier_name}</td>
            <td class="right"><fmt:formatNumber value="${r.quantity}"/></td>
            <!-- 入庫日を日本式フォーマットで表示 / 입고일을 일본식 포맷으로 표시 -->
            <td><fmt:formatDate value="${r.receive_date}" pattern="yyyy年MM月dd日"/></td>
            <td class="right"><fmt:formatNumber value="${r.available_to_return}"/></td>
            <td>
              <!-- 業務状態セレクト / 업무 상태 셀렉트 -->
              <select name="status_${r.receive_id}">
                <c:choose>
                  <c:when test="${not empty receiveStatusList}">
                    <%-- サービスから受け取った状態一覧で描画 / 서비스에서 받은 상태 목록으로 렌더 --%>
                    <c:forEach var="st" items="${receiveStatusList}">
                      <option value="${st}" <c:if test="${st == r.receive_status}">selected</c:if>>${st}</option>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <%-- フォールバック（状態一覧が無い場合の既定値） / 폴백(상태 목록이 없을 때 기본값) --%>
                    <option value="検収中"  <c:if test="${r.receive_status == '検収中'}">selected</c:if>>検収中</option>
                    <option value="正常"    <c:if test="${r.receive_status == '正常'}">selected</c:if>>正常</option>
                    <option value="入庫取消" <c:if test="${r.receive_status == '入庫取消'}">selected</c:if>>入庫取消</option>
                    <option value="返品処理" <c:if test="${r.receive_status == '返品処理'}">selected</c:if>>返品処理</option>
                  </c:otherwise>
                </c:choose>
              </select>
            </td>
          </tr>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </table>

  <!-- フッタ操作領域 / 푸터 조작 영역 -->
  <div style="margin-top:10px; display:flex; gap:8px; align-items:center;">
    <button type="submit" class="btn">選択項目の業務状態を保存</button> <%-- 선택항목 상태 저장 --%>
    <span class="muted">合計 <strong>${fn:length(receiveList)}</strong> 件</span> <%-- 件数表示 / 건수 표시 --%>
  </div>
</form>

</body>
</html>

