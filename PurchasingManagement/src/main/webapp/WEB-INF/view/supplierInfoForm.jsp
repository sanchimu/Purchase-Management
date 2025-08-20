<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先登録</title>
<style>  /* デザイン指定 */
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
    .btn-area {
        margin-top: 15px;
    }
    input[type="submit"], input[type="button"] {
        padding: 8px 15px;
        border: none;
        cursor: pointer;
        margin-right: 5px;
    }
    input[type="submit"] {
        background-color: #4CAF50;
        color: white;
    }
    input[type="button"] {
        background-color: #ccc;
    }
</style>
</head>
<body> <!-- 画面に表示される本文 -->

<h2>仕入先登録</h2>

<form action="<%=request.getContextPath()%>/insertsupplier.do" method="post">
<%-- formタグ : ブラウザで入力したデータをサーバーに送信するためのタグ
ここでは insertsupplier.do に POST方式でデータを送信
<%=request.getContextPath()%> は <%= %> 内のコード実行結果をHTMLに出力
request.getContextPathは現在のWebアプリケーションのコンテキストパスを返す。ここでは /PurchasingManagement を返す --%>
    <label for="supplier_id">仕入先ID</label>
<%-- labelタグ : 入力フィールドの説明を付けるタグ
for="supplier_id": このlabelがどのinputと結びつくかを指定
"仕入先ID"をクリックしても入力欄が選択される --%>
    <input type="text" id="supplier_id" name="supplier_id" required>
<%-- HTML入力フィールド。type="text" : 1行テキスト入力欄
id="supplier_id" : HTML文書内での識別名
name="supplier_id" : サーバーに送信するときのパラメータ名。サーバーはこのname値を使ってデータを読み取る
required : 未入力で送信しようとするとブラウザが警告を出す --%>

	<%--仕入先名入力欄、必須 --%>
    <label for="supplier_name">仕入先名</label>
    <input type="text" id="supplier_name" name="supplier_name" required>
	<%--連絡先入力欄、任意 --%>
    <label for="contact_number">連絡先</label>
    <input type="text" id="contact_number" name="contact_number">
	<%--住所入力欄、任意 --%>
    <label for="address">住所</label>
    <input type="text" id="address" name="address">

    <div class="btn-area">
        <input type="submit" value="登録">
        <%-- type="submit" : form内のボタンクリックでデータ送信
        value="登録" : ボタンに表示される文字 --%>
        <input type="button" value="一覧へ" onclick="location.href='<%=request.getContextPath()%>/listsupplier.do'">
        <%-- type="button" : 通常のボタン。フォーム送信機能なし
        onclick="location.href='...'" : ボタンクリック時にブラウザのURLを指定値に変更 --%>
    </div>
</form>

</body>
</html>
