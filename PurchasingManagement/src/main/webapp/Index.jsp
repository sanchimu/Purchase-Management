<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>구매 관리 시스템</title>
<style>
  body {
    margin: 0;
    padding: 0;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background-color: #f9f9f9;
  }

  /* 상단 메뉴 고정 */
  .header {
    position: fixed;
    top: 0;
    width: 100%;
    background-color: #ffffff;
    z-index: 1000;
    padding: 15px 0;
    border-bottom: 2px solid #e0e0e0;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  }

  .BigTitle {
    font-weight: bold;
    font-size: 28px;
    color: #333;
    margin-bottom: 10px;
  }

  .Menu {
    font-weight: bold;
    font-size: 18px;
    color: #555;
    background-color: #f2f2f2;
    border-radius: 8px;
    padding: 8px 16px;
    margin: 5px 8px;
    text-decoration: none;
    display: inline-block;
    transition: all 0.2s ease;
  }

  .Menu:hover,
  .Menu.active {
    background-color: #4CAF50;
    color: white;
    transform: scale(1.05);
  }

  /* 아래쪽 콘텐츠 영역 */
  .content {
    width: 100%;
    margin-top: 150px; /* 헤더 높이만큼 아래로 */
    height: calc(100vh - 150px);
    border: none;
  }
</style>
</head>
<body>

<div class="header">
  <div class="BigTitle">🎶😊 구 매 관 리 😊🎶</div>
  <a href="listProducts.do" class="Menu" target="mainFrame">❤️ 상품 관리</a>
  <a href="listsupplier.do" class="Menu" target="mainFrame">😉 공급 업체</a>
  <a href="requestList.do" class="Menu" target="mainFrame">👺 구매 요청</a>
  <a href="orderList.do" class="Menu" target="mainFrame">😎 발주서</a>
  <a href="listReceiveInfos.do" class="Menu" target="mainFrame">😢 입고 관리</a>
  <a href="returnInfoList.do" class="Menu" target="mainFrame">🥱 반품 관리</a>
</div>

<!-- 아래 콘텐츠 영역 -->
<iframe name="mainFrame" class="content" src="main.jsp"></iframe>

<script>
  const menuLinks = document.querySelectorAll('.Menu');

  menuLinks.forEach(link => {
    link.addEventListener('click', () => {
      menuLinks.forEach(l => l.classList.remove('active'));
      link.classList.add('active');
    });
  });

  // 초기 페이지 로드시 첫 메뉴 활성화 (옵션)
  window.addEventListener('load', () => {
    // 예: 첫 메뉴 활성화 우린 그냥 Main 띄울거라서 상관없음
    /* if(menuLinks.length > 0) {
      menuLinks[0].classList.add('active');
    } */
  });
</script>

</body>
</html>