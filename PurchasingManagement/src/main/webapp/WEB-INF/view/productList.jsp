<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchase.vo.Product" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>商品目録</title>
<style>
/* 테이블 스타일 */
table {
	width: 80%;
	border-collapse: collapse;
	margin: 20px auto;
}

th, td {
	border: 1px solid #ccc;
	padding: 8px;
	text-align: center;
}

th {
	background-color: #f2f2f2;
}

/* 버튼 스타일 */
.btn {
	padding: 4px 10px;
	background-color: #4CAF50;
	color: white;
	border: none;
	cursor: pointer;
	text-decoration: none;
}

.btn:hover {
	opacity: 0.8;
}

/* 상단 메뉴 레이아웃 */
.top-menu {
	width: 80%;
	margin: 20px auto;
	text-align: right;
}

.toolbar {
	width: 80%;
	margin: 10px auto;
	text-align: left;
}

/* 상태 배지 */
.badge-ok {
	padding: 2px 8px;
	border-radius: 10px;
	background: #e6f6e6;
}

.badge-stop {
	padding: 2px 8px;
	border-radius: 10px;
	background: #fdeaea;
}

.muted {
	opacity: .55;
}
</style>
<script>
function validateForm(){
    
    return true;
}
</script>

<script>
window.onload = function() { //경고창을 띄우기 위한 함수 (警告ウィンドウを出すための関数)
    <% if (request.getAttribute("success") != null) { %> //success값이 있을경우 경고창 발생 (alert) (success値がある場合、警告ウィンドウが発生(alert))
        alert("商品追加完了");
        document.querySelector("form").reset();
    <% } else if (request.getAttribute("NoSearchProductResult") != null) { %>//NoSearchProductResult값이 있을경우 경고창 발생 (alert) (NoSearch Product Result値がある場合、警告ウィンドウが発生(alert))
        alert("検索結果がありません。");
    <% } %>
}
</script>


</head>


<body>

<!-- 상단 메뉴 -->
<div class="top-menu">
    <a href="addProduct.do" class="btn">商品登録</a>
    <a href="javascript:location.reload();" class="btn">再照会</a>
</div>

<h2 style="text-align:center;">商品照会</h2>
<!-- 검색 폼 -->
<form action="searchProducts.do" method="get" style="width:80%; margin:20px auto; text-align:center;"><!-- submit 시 실행된 action과 데이터 전달 방식 (get,post)세팅 (submit時に実行されたactionとデータ伝達方式(get,post)セッティング) -->
	<!-- 아래는 검색창인데 검색 후 검색했던 값을 그대로 유지하기 위해 value에 param.~~를 사용했음 (以下は検索窓ですが、検索後に検索した値をそのまま維持するためにvalueにparam。~~を使う) -->
    商品ID : <input type="text" name="product_id" value="${param.product_id}" style="margin-right:10px; padding:4px;">
    商品名 : <input type="text" name="product_name" value="${param.product_name}" style="margin-right:10px; padding:4px;">
    分類 :
    <select name="category" style="margin-right:10px; padding:4px;">
        <option value="">-- 選択 --</option>
        <c:forEach items="${categoryList}" var="cat"><!-- foreach로 catogoryList에 있는 값이 다 나올때까지 반복되고 그 값은 cat에 저장됨 (foreachでcatogory Listにある値が全部出るまで繰り返し、その値はcatに保存される) -->
            <option value="${cat}" <c:if test="${param.category == cat}">selected</c:if>>${cat}</option>
            <!-- 반복될때마다 cat에 값이 저장되며 option에 값이 들어가게 되어 드롭박스를 생성 그리고 현재 선택된 카테고리 상태가 있을 경우 그것을 현상태로 유지 (<c:if test="${param.category == cat}">selected</c:if>)  -->
            <!-- 繰り返されるたびにcatに値が保存され、optionに値が入るようになってドロップボックスを生成し、そして現在選択されたカテゴリー状態がある場合、それを現状のまま維持  -->
        </c:forEach>
    </select>
    仕入先ID : <input type="text" name="supplier_id" value="${param.supplier_id}" style="margin-right:10px; padding:4px;">
    <input type="submit" value="検索" class="btn">
</form>
<!-- 상품 목록 테이블 -->
<h2 style="text-align:center;">商品目録</h2>
<form action="updateProducts.do" method="post" onsubmit="return validateForm();">
    <table>
        <tr>
            <th>商品ID</th>
            <th>商品名</th>
            <th>分類</th>
            <th>価格</th>
            <th>仕入先(仕入先ID)</th>
            <th>状態</th>
        </tr>
        <%
            List<Product> productList = (List<Product>) request.getAttribute("productList");//productList 키에 저장된 값을 가져와서 List<Product>형으로 변환해서 productList 객체에 저장
          																					//productListキーに保存された値を取得し、List<Product>型に変換してproductListオブジェクトに保存
            if(productList != null){ //productList가 있을 경우 (product Listがある場合)
                for (Product p : productList) { // productList를 하나씩 돌며 p에 넣으며 for문 실행 (product Listを一つずつ回りながらpに入れてfor文実行)
        %>
        <tr class="<%= "販売終了".equals(p.getProduct_status()) ? "muted" : "" %>">	<!-- 상태값이 販売終了 이면 muted고 아니면 빈 문자열인데 muted는 스타일임  -->
            <td><%= p.getProduct_id() %></td>
            <td><a href="modifyProduct.do?product_id=<%= p.getProduct_id() %>"><%= p.getProduct_name() %></a></td> <!-- 상품명 누르면 Handler 실행되도록 하려는것인데 이때 product_id도 함께 넘겨주기 위해  ?product_id를 기재 -->
            <td><%= p.getCategory() %></td> 
            <td><%= p.getPrice() %></td>
            <td><%= p.getSupplier_name() %> (<%= p.getSupplier_id() %>)</td>
            <td>
                <select name="<%= p.getProduct_id() %>"><!-- id에 맞는 상품 상태를 저장하기 위한 수단, 아래는 드롭박스에 띄울 상태값인데 하드 코딩이긴하지만 양이 적어서 이렇게 처리 -->
                										<!-- idに合った商品の状態を保存するための手段  -->
                    <option value="販売中" <%= "販売中".equals(p.getProduct_status()) ? "selected" : "" %>>販売中</option>
                    <option value="一時欠品" <%= "一時欠品".equals(p.getProduct_status()) ? "selected" : "" %>>一時欠品</option>
                    <option value="予約販売" <%= "予約販売".equals(p.getProduct_status()) ? "selected" : "" %>>予約販売</option>
                    <option value="販売停止" <%= "販売停止".equals(p.getProduct_status()) ? "selected" : "" %>>販売停止</option>
                    <option value="販売終了" <%= "販売終了".equals(p.getProduct_status()) ? "selected" : "" %>>販売終了</option>
                </select>
            </td>
        </tr>
        <% } } else { %> <!-- productList에 값이 없을 경우 (product Listに値がない場合)  -->
        <tr><td colspan="6">照会された商品がありません。</td></tr><!-- 조회정보 없다고 경고창 발생 (照会情報がないと警告ウィンドウが発生) -->
        <% } %>
    </table>
    <br>
    <div style="width:80%; margin:0 auto; text-align:left;">
    <input type="submit" class="btn" value="状態変更保存">
</div>
</form>
</body>
</html>