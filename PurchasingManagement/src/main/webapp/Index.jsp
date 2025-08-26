<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>購買管理システム</title>
<style>
  body {
    margin:0; padding:0; font-family:'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background:#f9f9f9;
    height:100vh;
    display:flex;
    flex-direction:column; /* 상단 고정 + 하단 flex 영역 */
  }

  /* 상단 고정 영역 */
  .header {
    height:60px;
    background:#fff;
    border-bottom:2px solid #e0e0e0;
    display:flex;
    align-items:center;
    justify-content:center;
    font-size:24px;
    font-weight:bold;
    color:#333;
    box-shadow:0 2px 8px rgba(0,0,0,0.05);
    z-index:1000;
  }

  /* 하단 영역: 좌측 메뉴 + 오른쪽 콘텐츠 */
  .main {
    flex:1; /* 남은 공간 */
    display:flex;
  }

  /* 좌측 메뉴 */
.sidebar {
  width:220px;
  background:#ffffff; 
  border-right:1px solid #ddd;
  display:flex;
  flex-direction:column;
  padding-top:20px;
  box-shadow:0 1px 3px rgba(0,0,0,0.05);
}

.Menu {
  display:flex;
  align-items:center;
  font-weight:500;
  font-size:15px;
  color:#333;
  background:transparent;       /* 배경 없음 */
  border-radius:3px;           /* 거의 직각 */
  border:1px solid #ccc;       /* 얇은 테두리 */
  padding:10px 16px;
  margin:6px 12px;
  text-decoration:none;
  transition: all 0.2s ease;
}

/* 아이콘과 텍스트 간격 */
.Menu::before {
  content: attr(data-icon);
  margin-right:8px;
}

/* Hover */
.Menu:hover:not(.active) {
  background:#E8F5E9; /* 연한 초록 */
  color:#388E3C;
  border-color:#4CAF50;
}

/* Active */
.Menu.active {
  background:#4CAF50; /* 진한 초록 */
  color:#fff;
  border-color:#388E3C;
}
  /* 오른쪽 콘텐츠 영역 */
  .content {
    flex:1;
    border:none;
    height:100%;
  }

</style>
</head>
<body>

<!-- 페이지 전체 상단 글귀 -->
<div class="header">購 買 管 理 ERP</div>

<!-- 좌측 메뉴 + 우측 콘텐츠 영역 -->
<div class="main">
  <div class="sidebar">
    <a href="<c:url value='/listProducts.do'/>"        class="Menu" target="mainFrame">商品管理</a>
    <a href="<c:url value='/listsupplier.do'/>"       class="Menu" target="mainFrame">供給会社</a>
    <a href="<c:url value='/requestList.do'/>"        class="Menu" target="mainFrame">購入要請</a>
    <a href="<c:url value='/orderSheetList.do'/>"     class="Menu" target="mainFrame">発注書</a>
    <a href="<c:url value='/listReceiveInfos.do'/>"   class="Menu" target="mainFrame">入庫管理</a>
    <a href="<c:url value='/returnInfoList.do'/>"     class="Menu" target="mainFrame">返品管理</a>
  </div>

  <iframe name="mainFrame" class="content" src="<c:url value='/main.jsp'/>"></iframe>
</div>

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