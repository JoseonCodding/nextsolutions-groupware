<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Hello JSP</title>
</head>
<body>
    <h2>Hello, ${name}</h2>
    
    <img src="${pageContext.request.contextPath}/img/lioncry.jpg" alt="로고 이미지" style="width:200px; height:auto;" />
</body>
</html>