<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先登録</title>
<style>
    /* 화면 기본 스타일 */
    /* 画面の基本スタイル */
    body { font-family: Arial, sans-serif; margin: 20px; }
    h2 { color: #333; }
    form { width: 400px; }
    label { display: block; margin-top: 10px; }
    input[type="text"] {
        width: 100%;
        padding: 8px;
        margin-top: 4px;
        box-sizing: border-box;
    }
    .btn-area { margin-top: 15px; }
    input[type="submit"], input[type="button"] {
        padding: 8px 15px;
        border: none;
        cursor: pointer;
        margin-right: 5px;
    }
    input[type="submit"] { background-color: #4CAF50; color: white; }
    input[type="button"] { background-color: #ccc; }
</style>
</head>
<body>

<h2>仕入先登録</h2>

<!-- 신규 공급업체 등록 폼. /insertsupplier.do 로 POST 전송 -->
<!-- 新規仕入先登録フォーム。/insertsupplier.do にPOST送信 -->
<form action="<%=request.getContextPath()%>/insertsupplier.do" method="post">

    <!-- 공급업체 ID (필수 입력) -->
    <!-- 仕入先ID（必須入力） -->
    <label for="supplier_id">仕入先ID</label>
    <input type="text" id="supplier_id" name="supplier_id" required>

    <!-- 공급업체명 (필수 입력) -->
    <!-- 仕入先名（必須入力） -->
    <label for="supplier_name">仕入先名</label>
    <input type="text" id="supplier_name" name="supplier_name" required>

    <!-- 연락처 (선택 입력) -->
    <!-- 連絡先（任意入力） -->
    <label for="contact_number">連絡先</label>
    <input type="text" id="contact_number" name="contact_number">

    <!-- 주소 (선택 입력) -->
    <!-- 住所（任意入力） -->
    <label for="address">住所</label>
    <input type="text" id="address" name="address">

    <!-- 버튼 영역 -->
    <!-- ボタンエリア -->
    <div class="btn-area">
        <!-- 제출 버튼: 폼 입력값을 서버로 전송 -->
        <!-- 送信ボタン: フォーム入力値をサーバーに送信 -->
        <input type="submit" value="登録">

        <!-- 목록 이동 버튼: listsupplier.do 로 이동 -->
        <!-- 一覧移動ボタン: listsupplier.do へ移動 -->
        <input type="button" value="一覧へ" onclick="location.href='<%=request.getContextPath()%>/listsupplier.do'">
    </div>
</form>

</body>
</html>
