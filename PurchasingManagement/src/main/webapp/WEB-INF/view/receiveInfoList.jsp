<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>入庫リスト</title>
<style>
  body{font-family:Arial,sans-serif;margin:20px;}
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

<!-- flatpickr -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/ja.js"></script>

<script>
  function toggleAll(source){
    const boxes=document.querySelectorAll('input[name="receiveIds"]');
    boxes.forEach(cb=>cb.checked=source.checked);
  }
  function ensureChecked(){
    const checked=document.querySelectorAll('input[name="receiveIds"]:checked');
    if(checked.length===0){ alert('1つ以上の項目を選択してください。'); return false; }
    return confirm('選択した項目の業務状態を保存しますか？');
  }

  // flatpickr適用 (日本語ロケール)
  document.addEventListener("DOMContentLoaded", function() {
    flatpickr("input[name='fromDate']", {
      locale: "ja",
      altInput: true,
      altFormat: "Y年m月d日",
      dateFormat: "Y-m-d"    
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

<h2>入庫リスト</h2>

<div class="toolbar">
  <a class="btn" href="<c:url value='/addReceiveInfo.do'/>">入庫登録</a>
  <a class="btn" href="<c:url value='/listReceiveInfos.do'/>">再読み込み</a>

  <!-- 簡易検索 -->
  <form action="<c:url value='/listReceiveInfos.do'/>" method="get">
    <input type="text" name="productName" value="${fn:escapeXml(param.productName)}" placeholder="商品名"/>
    <input type="text" name="supplierName" value="${fn:escapeXml(param.supplierName)}" placeholder="仕入先"/>
    <input type="text" name="fromDate" value="${param.fromDate}" placeholder="開始日"/>
    ~
    <input type="text" name="toDate" value="${param.toDate}" placeholder="終了日"/>
    <input type="submit" class="btn" value="検索"/>
  </form>
</div>

<form action="<c:url value='/updateReceiveInfos.do'/>" method="post" onsubmit="return ensureChecked();">
  <table>
    <tr>
      <th><input type="checkbox" onclick="toggleAll(this)"></th>
      <th>入庫ID</th>
      <th>注文ID</th>
      <th>商品ID</th>
      <th>商品名</th>
      <th>仕入先</th>
      <th class="right">数量</th>
      <th>入庫日</th>
      <th class="right">返品可能</th>
      <th>業務状態</th>
    </tr>

    <c:choose>
      <c:when test="${empty receiveList}">
        <tr><td colspan="10">該当する入庫情報がありません。</td></tr>
      </c:when>
      <c:otherwise>
        <c:forEach var="r" items="${receiveList}">
          <tr>
            <td><input type="checkbox" name="receiveIds" value="${r.receive_id}"></td>
            <td>${r.receive_id}</td>
            <td>${r.order_id}</td>
            <td>${r.product_id}</td>
            <td>${empty r.product_name ? '-' : r.product_name}</td>
            <td>${empty r.supplier_name ? '-' : r.supplier_name}</td>
            <td class="right"><fmt:formatNumber value="${r.quantity}"/></td>
            <!-- 日本式フォーマットで表示 -->
            <td><fmt:formatDate value="${r.receive_date}" pattern="yyyy年MM月dd日"/></td>
            <td class="right"><fmt:formatNumber value="${r.available_to_return}"/></td>
            <td>
              <select name="status_${r.receive_id}">
                <c:choose>
                  <c:when test="${not empty receiveStatusList}">
                    <c:forEach var="st" items="${receiveStatusList}">
                      <option value="${st}" <c:if test="${st == r.receive_status}">selected</c:if>>${st}</option>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
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

  <div style="margin-top:10px; display:flex; gap:8px; align-items:center;">
    <button type="submit" class="btn">選択項目の業務状態を保存</button>
    <span class="muted">合計 <strong>${fn:length(receiveList)}</strong> 件</span>
  </div>
</form>

</body>
</html>
