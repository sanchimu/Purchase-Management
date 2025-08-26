<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>Welcome – 購買管理システム</title>
<style>
  :root{
    --ink:#0f172a; --muted:#64748b;
    --bg:#f1f5f9; --panel:#ffffff; --line:#e5e7eb;
    --shadow:0 8px 24px rgba(2,6,23,.08);
    --brand:#1f3a8a; --accent:#0ea5e9;
  }
  *{box-sizing:border-box}
  html,body{height:100%}
  body{
    margin:0; background:var(--bg); color:var(--ink);
    font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Noto Sans JP",sans-serif;
    display:flex; align-items:center; justify-content:center;
    padding:40px 20px;
  }
  .card{
    max-width:900px; width:100%;
    background:var(--panel);
    border:1px solid var(--line);
    border-radius:20px;
    box-shadow:var(--shadow);
    text-align:center;
    padding:60px 40px;
    position:relative;
    overflow:hidden;
  }
  .card::before{
    content:""; position:absolute; inset:-80px -40px auto auto;
    width:700px; height:280px;
    background:
      radial-gradient(600px 200px at 100% 0, rgba(59,130,246,.12), transparent 60%),
      radial-gradient(400px 160px at 70% 40%, rgba(79,70,229,.10), transparent 60%);
    pointer-events:none;
  }
  h1{margin:0; font-size:32px; letter-spacing:.04em; font-weight:800;}
  p.lead{margin:16px 0 0; font-size:16px; color:var(--muted);}
  .foot{margin-top:32px; font-size:13px; color:var(--muted);}
</style>
</head>
<body>
  <div class="card">
    <h1>ようこそ、購買管理システムへ</h1>
    <p class="lead">
      発注・入庫・返品までの購買プロセスを<br/>
      一元的かつ効率的に管理するためのシステムです。
    </p>
    <div class="foot">
      PurchasingManagement ・ JSP/Servlet ・ Oracle Database
    </div>
  </div>
</body>
</html>
