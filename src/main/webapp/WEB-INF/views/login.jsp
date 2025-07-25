<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>로그인</title>
</head>
<body>
    <h2>로그인</h2>
    <form method="post" action="/login">
        <input type="text" name="username" placeholder="아이디" required />
        <br>
        <input type="password" name="password" placeholder="비밀번호" required />
        <br>
        <button type="submit">로그인</button>
    </form>
    <c:if test="${param.error != null}">
        <p style="color:red;">로그인 실패! 아이디 또는 비밀번호를 확인하세요.</p>
    </c:if>
    <c:if test="${param.logout != null}">
        <p style="color:green;">로그아웃 되었습니다.</p>
    </c:if>
</body>
</html>
