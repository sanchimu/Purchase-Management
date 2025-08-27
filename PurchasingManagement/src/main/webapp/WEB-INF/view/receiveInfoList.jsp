<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> <%-- JSP 기본 설정 / JSP基本設定 --%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %> <%-- 자주 쓰는 JSTL 코어 태그 / よく使うJSTLコア --%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- 날짜·숫자 포맷 전담 / 日付と数値のフォーマット --%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %> <%-- 문자열 함수들 / 文字列ユーティリティ --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/> <%-- 컨텍스트 경로 보관. 링크 만들 때 편함 / コンテキストパス保持 --%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>入庫リスト</title> <%-- 페이지 제목. 입고 목록 화면 / 画面タイトル：入庫リスト --%>

<style>
  /* 페이지 공통 가벼운 스타일 / ページ共通の軽いスタイル */
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

<!-- 달력 입력용 flatpickr / カレンダー入力UI -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/ja.js"></script>

<script>
  // ===== 체크박스 관련 스크립트 / チェックボックス操作 =====

  // 헤더 체크 누르면 전체 선택·해제 / ヘッダチェックで全選択・解除
  function toggleAll(source){
    const boxes=document.querySelectorAll('input[name="receiveIds"]');
    boxes.forEach(cb=>cb.checked=source.checked);
  }

  // 아무것도 안 고르면 전송 막기 / 何も選んでなければ送信しない
  function ensureChecked(){
    const checked=document.querySelectorAll('input[name="receiveIds"]:checked');
    if(checked.length===0){
      alert('1つ以上の項目を選択してください。'); // 한 개 이상 골라줘
      return false;
    }
    return confirm('選択した項目の業務状態を保存しますか？'); // 선택한 상태로 저장할까?
  }

  // ===== 날짜 입력 flatpickr 세팅 / flatpickr 初期化 =====
  document.addEventListener("DOMContentLoaded", function() {
    flatpickr("input[name='fromDate']", {
      locale: "ja",
      altInput: true,               // 보기 좋은 표시용 입력 추가
      altFormat: "Y年m月d日",       // 화면 표시 형식
      dateFormat: "Y-m-d"           // 서버로 넘어가는 실제 값 형식
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

<h2>入庫リスト</h2> <%-- 입고 데이터 한눈에 보는 화면 / 入庫データ一覧 --%>

<div class="toolbar">
  <!-- 신규 입고 등록 버튼 / 新規入庫登録ボタン -->
  <!-- c:url 쓰면 자동으로 컨텍스트 경로 붙음 / c:url は自動でコンテキスト付与 -->
  <a class="btn" href="<c:url value='/addReceiveInfo.do'/>">入庫登録</a>

  <!-- 간단 검색 폼: 상품명·공급업체·기간 / 簡易検索フォーム -->
  <form action="<c:url value='/listReceiveInfos.do'/>" method="get">
    <input type="text" name="productName" value="${fn:escapeXml(param.productName)}" placeholder="商品名"/> <%-- 상품명으로 찾기 --%>
    <input type="text" name="supplierName" value="${fn:escapeXml(param.supplierName)}" placeholder="仕入先"/> <%-- 공급업체로 찾기 --%>
    <input type="text" name="fromDate" value="${param.fromDate}" placeholder="開始日"/> <%-- 시작일 --%>
    ~
    <input type="text" name="toDate" value="${param.toDate}" placeholder="終了日"/> <%-- 종료일 --%>
    <input type="submit" class="btn" value="検索"/> <%-- 검색 실행 --%>
  </form>
</div>

<!-- 상태 일괄 저장 폼. 체크된 행만 반영 / 一括更新フォーム（チェック行のみ反映） -->
<form action="<c:url value='/updateReceiveInfos.do'/>" method="post" onsubmit="return ensureChecked();">
  <table>
    <tr>
      <th><input type="checkbox" onclick="toggleAll(this)"></th> <%-- 전체 선택 / 全選択 --%>
      <th>入庫ID</th>   <%-- 입고ID --%>
      <th>注文ID</th>   <%-- 주문ID --%>
      <th>商品ID</th>   <%-- 상품ID --%>
      <th>商品名</th>   <%-- 상품명 --%>
      <th>仕入先</th>   <%-- 공급업체 --%>
      <th class="right">数量</th> <%-- 수량 --%>
      <th>入庫日</th>   <%-- 입고일 --%>
      <th class="right">返品可能</th> <%-- 반품 가능 수량 --%>
      <th>業務状態</th> <%-- 업무 상태 --%>
    </tr>

    <c:choose>
      <c:when test="${empty receiveList}">
        <tr><td colspan="10">該当する入庫情報がありません。</td></tr> <%-- 데이터 없음 안내 / データなし --%>
      </c:when>
      <c:otherwise>
        <%-- 목록 렌더링 / 明細描画 --%>
        <c:forEach var="r" items="${receiveList}">
          <tr>
            <td><input type="checkbox" name="receiveIds" value="${r.receive_id}"></td> <%-- 행 선택 --%>
            <td>${r.receive_id}</td>
            <td>${r.order_id}</td>
            <td>${r.product_id}</td>
            <td>${empty r.product_name ? '-' : r.product_name}</td>
            <td>${empty r.supplier_name ? '-' : r.supplier_name}</td>
            <td class="right"><fmt:formatNumber value="${r.quantity}"/></td>
            <!-- 입고일은 일본식 표기로 살짝 보기 좋게 / 入庫日は和式フォーマットで見やすく -->
            <td><fmt:formatDate value="${r.receive_date}" pattern="yyyy年MM月dd日"/></td>
            <td class="right"><fmt:formatNumber value="${r.available_to_return}"/></td>
            <td>
              <!-- 상태 셀렉트 박스 / 業務状態セレクト -->
              <select name="status_${r.receive_id}">
                <c:choose>
                  <c:when test="${not empty receiveStatusList}">
                    <%-- 서비스에서 받은 상태 목록대로 표시 / サービス提供のリストで描画 --%>
                    <c:forEach var="st" items="${receiveStatusList}">
                      <option value="${st}" <c:if test="${st == r.receive_status}">selected</c:if>>${st}</option>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <%-- 예비 값: 상태 목록이 없을 때 대비 / フォールバック --%>
                    <option value="受入済"  <c:if test="${r.receive_status == '受入済'}">selected</c:if>>受入済</option>
                    <option value="正常"    <c:if test="${r.receive_status == '正常'}">selected</c:if>>正常</option>
                    <option value="入荷取消" <c:if test="${r.receive_status == '入荷取消'}">selected</c:if>>入荷取消</option>
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

  <!-- 하단 액션 영역 / フッター操作領域 -->
  <div style="margin-top:10px; display:flex; gap:8px; align-items:center;">
    <button type="submit" class="btn">選択項目の業務状態を保存</button> <%-- 선택한 것만 저장 --%>
    <span class="muted">合計 <strong>${fn:length(receiveList)}</strong> 件</span> <%-- 총 건수 표시 --%>
  </div>
</form>

</body>
</html>
