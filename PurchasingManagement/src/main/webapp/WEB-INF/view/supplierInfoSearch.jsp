<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先検索</title>
<style>
  /* ページ全体の基本スタイル */
  body { font-family: Arial, sans-serif; margin: 20px; }
  h2 { color: #333; }

  /* 検索ツールバー（検索フォームやボタンを横並びにする） */
  .toolbar { display:flex; gap:12px; align-items:center; margin:10px 0 16px; }
  .toolbar form { display:flex; gap:8px; align-items:center; }

  /* 入力フィールドとボタンのデザイン */
  input[type="text"] { padding:6px 8px; }
  input[type="submit"], a.btn {
      padding:6px 10px;
      border:1px solid #ccc;
      background:#f7f7f7;
      text-decoration:none;
      color:#333;
      cursor:pointer;
  }

  /* メッセージ用スタイル（警告や案内を表示） */
  .msg { margin:8px 0; padding:8px 10px; background:#fff8d6; border:1px solid #eedc82; }

  /* テーブルスタイル（検索結果一覧） */
  table { border-collapse: collapse; width: 100%; max-width: 900px; }
  th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
  th { background:#f2f2f2; }
</style>
</head>
<body>

<!-- ページタイトル -->
<h2>仕入先検索（名前）</h2>

<!-- 検索ツールバー -->
<div class="toolbar">
  <!-- POST方式でハンドラー(searchsupplier.do)に送信するフォーム -->
  <form action="<c:url value='/searchsupplier.do'/>" method="post">
    <!-- 入力ラベルとテキストフィールド -->
    <label for="supplier_name">仕入先名</label>
    <input type="text" id="supplier_name" name="supplier_name"
           placeholder="仕入先名を入力"  value="${supplier_name}" /> 
            <!-- ユーザーが未入力の時に表示されるヒント -->
            <!-- 入力済みの値を保持 -->

    <!-- 検索実行ボタン -->
    <input type="submit" value="検索" />

    <!-- 入力内容をリセットして再検索できるボタン -->
    <a class="btn" href="<c:url value='/searchsupplier.do'/>">リセット</a>
  </form>

  <!-- 新規登録ページへ移動 -->
  <a class="btn" href="<c:url value='/insertsupplier.do'/>">仕入先登録</a>
  <!-- 一覧ページへ戻る -->
  <a class="btn" href="<c:url value='/listsupplier.do'/>">一覧へ</a>
</div>

<!-- サーバーからメッセージが渡された場合に表示（例: エラーや案内文） -->
<c:if test="${not empty message}">
  <div class="msg">${message}</div>
</c:if>

<!-- 検索結果(supplierList)が存在する場合にテーブル表示 -->
<c:if test="${not empty supplierList}">
  <table>
    <tr>
      <th>仕入先ID</th>
      <th>仕入先名</th>
      <th>連絡先</th>
      <th>住所</th>
      <th>削除</th>
    </tr>

    <!-- 検索結果を1行ずつループ表示 -->
    <c:forEach var="supplier" items="${supplierList}">
      <tr>
        <!-- 各カラムに仕入先情報を出力 -->
        <td>${supplier.supplier_id}</td>
        <td>${supplier.supplier_name}</td>
        <td>${supplier.contact_number}</td>
        <td>${supplier.address}</td>
        <td>
          <!-- 削除ボタン付きフォーム（POST送信でdeletesupplier.doへ） -->
          <form action="<c:url value='/deletesupplier.do'/>" method="post" style="margin:0">
            <!-- 削除対象のIDをhiddenで送信 -->
            <input type="hidden" name="supplier_id" value="${supplier.supplier_id}"/>
            <!-- 削除ボタン（確認ダイアログ付き） -->
            <input type="submit" value="削除" onclick="return confirm('削除しますか？');"/>
          </form>
        </td>
      </tr>
    </c:forEach>
  </table>
</c:if>

</body>
</html>
