<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kdt.KDT_PJT.sample.dto.UserDTO" %>
<html>
<head>
    <title>사용자 관리</title>
    <style>
        table {
            border-collapse: collapse;
            width: 100%;
        }
        table, th, td {
            border: 1px solid #ccc;
        }
        th, td {
            padding: 8px;
            text-align: center;
        }
        .btn {
            padding: 6px 12px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        .btn-red {
            background-color: #e74c3c;
        }
    </style>
</head>
<body>

<h2>사용자 목록</h2>

<!-- 등록 버튼 -->
<p>
    <a href="<%= request.getContextPath() %>/users/new" class="btn">사용자 등록</a>
</p>

<!-- 테이블 -->
<table>
    <thead>
    <tr>
        <th>사번</th>
        <th>이름</th>
        <th>휴대폰 번호</th>
        <th>부서명</th>
        <th>직위</th>
        <th>권한</th>
        <th>상세보기</th>
        <th>삭제</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<UserDTO> users = (List<UserDTO>) request.getAttribute("users");
        if (users != null && !users.isEmpty()) {
            for (UserDTO user : users) {
    %>
        <tr>
            <td><%= user.getEmpNo() %></td>
            <td><%= user.getName() %></td>
            <td><%= user.getPhone() %></td>
            <td><%= user.getDepartmentName() %></td>
            <td><%= user.getPosition() %></td>
            <td><%= user.getRole() %></td>
            <td>
                <a href="<%= request.getContextPath() %>/users/<%= user.getEmpNo() %>" class="btn">상세</a>
            </td>
            <td>
                <form action="<%= request.getContextPath() %>/users/<%= user.getEmpNo() %>/delete" method="post" style="display:inline;">
                    <button type="submit" class="btn btn-red">삭제</button>
                </form>
            </td>
        </tr>
    <%
            }
        } else {
    %>
        <tr>
            <td colspan="8">등록된 사용자가 없습니다.</td>
        </tr>
    <%
        }
    %>
    </tbody>
</table>

</body>
</html>
