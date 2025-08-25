<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- 
   발주서 목록 화면 (DB 일본어 상태값과 일치)
   発注書一覧画面（DBの日本語ステータスと一致）

    orderList : OrderSheetDTO 목록 / 一覧
    includeHidden : 중단(X) 포함 여부 / 停止(X)の表示有無
    orderStatusList : 업무상태 드롭다운 목록 (DB CHECKと一致: 発注依頼/承認/発注/一部入荷/完了/取消/保留)
--%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>発注書一覧</title>

<style>
  /*  화면 기본 스타일 /  画面の基本スタイル */
  body{font-family:Arial,Helvetica,sans-serif;margin:20px;}
  h2{margin:0 0 12px;}
  .toolbar{display:flex;gap:8px;align-items:center;margin:8px 0 16px;flex-wrap:wrap}
  .btn{padding:6px 10px;border:1px solid #ccc;background:#f7f7f7;cursor:pointer;text-decoration:none;color:#333;display:inline-block}
  table{border-collapse:collapse;width:100%}
  th,td{border:1px solid #ddd;padding:8px;text-align:center}
  th{background:#f2f2f2}
  .pill{display:inline-block;min-width:40px;padding:2px 8px;border-radius:999px;font-size:12px}
  .pill-a{background:#e7f6e9;color:#1c7c2c} /*  진행중(A) /  進行中(A) */
  .pill-x{background:#fdeaea;color:#c62828} /*  중단(X)   /  停止(X)   */
</style>

<script>
  //  전체 체크박스 토글 /  一括チェック切替
  function toggleAll(src){
    document.querySelectorAll('input[name="orderIds"]').forEach(cb=>cb.checked=src.checked);
  }
</script>
</head>
<body>

<h2>発注書一覧</h2>

<div class="toolbar">
  <%--
      '停止含む' 체크 시 즉시 자동 제출되어 목록이 갱신됨
      '停止含む' チェック時に即時自動送信し一覧を更新
  --%>
  <form method="get" action="<c:url value='/orderSheetList.do'/>"
        style="display:flex;align-items:center;gap:6px">
    <label>
      <input type="checkbox"
             name="includeHidden"
             value="1"
             onchange="this.form.submit()"
             <c:if test="${includeHidden}">checked</c:if> />
      停止含む
    </label>

  <%--  발주서 등록 화면으로 이동 /  発注書登録画面へ遷移 --%>
  <a class="btn" href="<c:url value='/addOrderSheet.do'/>">発注書登録</a>
</div>

<form method="post">
  <table>
    <tr>
      <th style="width:40px"><input type="checkbox" onclick="toggleAll(this)"></th>
      <th>発注ID</th>
      <th>発注日</th>
      <th>商品ID</th>
      <th>商品名</th>
      <th>申請数量</th>
      <th>申請者</th>
      <th>供給業者ID</th>
      <th>供給業者名</th>
      <th>業務状態</th>
      <th>表示状態</th>
    </tr>

    
    <c:choose>
      <c:when test="${empty orderList}">
        <tr><td colspan="11">該当する発注書がありません。</td></tr>
      </c:when>

      <c:otherwise>
        <%-- 
           발주서 + 조인된 정보 행 렌더링
           発注書＋結合済み情報の行レンダリング
        --%>
        <c:forEach var="o" items="${orderList}">
          <tr>
            <%--  일괄 작업용 체크박스 /  一括操作用チェック --%>
            <td><input type="checkbox" name="orderIds" value="${o.order_id}"></td>

            <%--  발주 기본 필드 /  発注の基本フィールド --%>
            <td>${o.order_id}</td>
            <td><fmt:formatDate value="${o.order_date}" pattern="yyyy-MM-dd"/></td>

            <%--  구매요청/상품에서 끌어온 키 /  購買申請/商品からのキー --%>
            <td>${o.product_id}</td>
            <td>${o.product_name}</td>
            <td>${o.request_quantity}</td>
            <td>${o.requester_name}</td>

            <%--  공급업체 정보 /  供給業者情報 --%>
            <td>${o.supplier_id}</td>
            <td>${o.supplier_name}</td>

            <%-- 
               업무상태 드롭다운
                   ※ orderStatusList는 서비스에서 DB CHECK와 동일한 목록 제공
                   (発注依頼/承認/発注/一部入荷/完了/取消/保留)
               業務状態ドロップダウン
                   ※ orderStatusList はサービス層で DB CHECK と同一リストを提供
            --%>
            <td>
              <select name="status_${o.order_id}">
                <c:forEach var="st" items="${orderStatusList}">
                  <option value="${st}" <c:if test="${st == o.order_status}">selected</c:if>>${st}</option>
                </c:forEach>
              </select>
            </td>

            <%-- 
               표시상태 A/X 뱃지
                   빈값(NULL)도 A로 간주하여 진행중 처리
               表示状態 A/X のバッジ
                   空(NULL)もAとして進行中扱い
            --%>
            <td>
              <c:choose>
                <c:when test="${empty o.row_status || o.row_status == 'A'}">
                  <span class="pill pill-a">進行中</span>
                </c:when>
                <c:otherwise>
                  <span class="pill pill-x">停止</span>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </table>

  <%-- 
     하단 일괄 버튼들
           상태 저장: 선택 행의 업무상태를 업데이트
           A/X 전환: 표시상태 토글
     下部の一括ボタン
           状態保存: 選択行の業務状態を更新
           A/X 切替: 表示状態トグル
  --%>
  <div style="margin-top:10px;display:flex;gap:8px;flex-wrap:wrap;">
    <button type="submit"
            class="btn"
            formaction="<c:url value='/updateOrderSheet.do'/>"
            formmethod="post">
      選択項目の業務状態を保存
    </button>

    <button type="submit"
            class="btn"
            formaction="<c:url value='/orderSheetStopAX.do'/>"
            formmethod="post">
      選択項目を停止
    </button>

    <button type="submit"
            class="btn"
            formaction="<c:url value='/orderSheetResumeAX.do'/>"
            formmethod="post">
      選択項目を再開
    </button>
  </div>
</form>

</body>
</html>
