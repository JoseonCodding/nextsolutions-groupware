<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

<!-- Swiper CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
<!-- Swiper JS -->
<script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>



<!-- 최신 버전 jQuery CDN (Google CDN 또는 jsDelivr) -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<!-- SweetAlert2 CDN -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>


<html>
<head>
    <title>Hello JSP</title>
</head>
<body>

	<!-- 
	
	localhost:8080/sampleJspMvc/api/test 호출시  
	model.addAttribute("name", "너의 이름은?"); 
	현재 name 에는 "너의 이름은?" 문자열 값이 담겨있다.
	-->
	<br>
    <h2 class="text-center">${name} 여러분 환영합니다~!</h2>
    <br>
  <%--   <img src="${pageContext.request.contextPath}/img/lioncry.jpg"   style="width:200px; height:auto;" class="d-block mx-auto" /> --%>
    <img src="<c:url value='/img/lioncry.jpg' />" style="width:200px; height:auto;" class="d-block mx-auto" />

<div class="text-center mt-3">
  <a href="/sampleJspMvc/home" class="btn btn-success">샘플 페이지로 이동</a>
</div>




</body>



	
</html>