<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>購買管理システム</title>
<style>
  body { margin:0; padding:0; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background:#f9f9f9; }
  .header { position:fixed; top:0; width:100%; background:#fff; z-index:1000; padding:15px 0; border-bottom:2px solid #e0e0e0; text-align:center; box-shadow:0 2px 8px rgba(0,0,0,0.05); }
  .BigTitle { font-weight:bold; font-size:28px; color:#333; margin-bottom:10px; }
  .Menu { font-weight:bold; font-size:18px; color:#555; background:#f2f2f2; border-radius:8px; padding:8px 16px; margin:5px 8px; text-decoration:none; display:inline-block; transition:all .2s ease; }
  .Menu:hover, .Menu.active { background:#4CAF50; color:#fff; transform:scale(1.05); }
  .content { width:100%; margin-top:150px; height:calc(100vh - 150px); border:none; }
</style>
</head>
<body>

<div class="header">
  <div class="BigTitle">🎶😊 購 買 管 理 😊🎶</div>

  <!-- 컨트롤러(.do)로 이동하도록 모두 c:url 사용 -->
  <a href="<c:url value='/listProducts.do'/>"        class="Menu" target="mainFrame">❤️ 商品管理</a>
  <a href="<c:url value='/listsupplier.do'/>"        class="Menu" target="mainFrame">😉 供給会社</a>
  <a href="<c:url value='/requestList.do'/>"         class="Menu" target="mainFrame">👺 購入要請</a>
  <a href="<c:url value='/orderSheetList.do'/>"      class="Menu" target="mainFrame">😎 発注書</a>
  <a href="<c:url value='/listReceiveInfos.do'/>"    class="Menu" target="mainFrame">😢 入庫管理</a> <!-- ← 여기 핵심 수정 -->
  <a href="<c:url value='/returnInfoList.do'/>"      class="Menu" target="mainFrame">🥱 返品管理</a>
</div>

<!-- 아래 콘텐츠 영역 -->
<iframe name="mainFrame" class="content" src="<c:url value='/main.jsp'/>"></iframe>

<script>
  const menuLinks = document.querySelectorAll('.Menu');
  menuLinks.forEach(link => {
    link.addEventListener('click', () => {
      menuLinks.forEach(l => l.classList.remove('active'));
      link.classList.add('active');
    });
  });
</script>

</body>
</html>
