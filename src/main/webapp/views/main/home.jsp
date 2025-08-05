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

    <jsp:include page="../_include/header.jsp" flush="false" />

    <div style="display:flex;">
      <!-- 사이드바 include -->
      <div style="width:220px;">
        <jsp:include page="../_include/sidebar.jsp" flush="false" />
      </div>
      <!-- 본문 영역 -->
      <div style="flex:1; padding:30px;">

        <!-- 기존의 회원 정보 입력 폼, 테이블 등 모든 내용 여기에 -->

	<br>    
<div class="container mt-5" style="max-width: 500px;">
    <h3 class="mb-4 text-center">회원 정보 입력</h3>

    <form id="mvcSample" action="/sampleJspMvc/api/saveProc" method="post">
        <div class="mb-3">
            <label for="kornFlnm" class="form-label">성명</label>
            <input type="text" class="form-control" id="kornFlnm" name="kornFlnm" placeholder="이름 입력">
        </div>

        <div class="mb-3">
            <label for="mblTelno" class="form-label">전화번호</label>
            <input type="text" class="form-control" id="mblTelno" name="mblTelno" placeholder="01012345678">
        </div>

        <div class="mb-3">
            <label for="emailAddr" class="form-label">이메일</label>
            <input type="email" class="form-control" id="emailAddr" name="emailAddr" placeholder="example@email.com">
        </div>

        <div class="d-grid">
            <button type="submit" class="btn btn-primary">저장(서브밋)</button>
        </div>
        <br>
        <div class="d-grid">
            <button type="button" class="btn btn-primary" onclick="test()">저장(AJAX)</button>
        </div>
        
    </form>
</div>

<br>
<div class="container mt-5">
  <div class="row">
    <!-- 회원 상세 (왼쪽) -->
    <div class="col-md-4 mb-4">
      <div class="card shadow-lg">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
          <h5 class="mb-0">회원 상세</h5>
          <div>
            <button id="editBtn" class="btn btn-warning btn-sm me-1">
              <i class="bi bi-pencil-square"></i> 수정
            </button>
            <button id="saveBtn" class="btn btn-success btn-sm me-1" style="display:none;">
              <i class="bi bi-check-lg"></i> 저장
            </button>
            <button id="cancelBtn" class="btn btn-secondary btn-sm" style="display:none;">
              <i class="bi bi-x-lg"></i> 취소
            </button>
            <button id="deleteBtn" class="btn btn-danger btn-sm">
              <i class="bi bi-trash"></i> 삭제
            </button>
          </div>
        </div>
        <div class="card-body">
          <div class="row align-items-center">
            <div class="col-12 text-center mb-3">
              <img id="detailPhoto" src="" alt="회원 사진" class="rounded-circle border" style="width:120px; height:120px; object-fit:cover;">
            </div>
            <div class="col-12">
              <dl class="row" id="detailView">
                <dt class="col-sm-4 fw-semibold">성명</dt>
                <dd id="detailKornFlnm" class="col-sm-8"></dd>
                <dt class="col-sm-4 fw-semibold">전화번호</dt>
                <dd id="detailMblTelno" class="col-sm-8"></dd>
                <dt class="col-sm-4 fw-semibold">이메일</dt>
                <dd id="detailEmlAddr" class="col-sm-8"></dd>
                <dt class="col-sm-4 fw-semibold">최초등록일</dt>
                <dd id="detailFrstRegDt" class="col-sm-8"></dd>
              </dl>
              <form id="editForm" style="display:none;">
                <div class="mb-2">
                  <label class="form-label">사진</label>
                  <input type="file" id="editPhoto" class="form-control" accept="image/*">
                </div>
                <div class="mb-2">
                  <label class="form-label">성명</label>
                  <input type="text" id="editKornFlnm" class="form-control">
                </div>
                <div class="mb-2">
                  <label class="form-label">전화번호</label>
                  <input type="text" id="editMblTelno" class="form-control">
                </div>
                <div class="mb-2">
                  <label class="form-label">이메일</label>
                  <input type="email" id="editEmlAddr" class="form-control">
                </div>
                <div class="mb-2">
                  <label class="form-label">최초등록일</label>
                  <input type="text" id="editFrstRegDt" class="form-control" readonly>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 회원 목록 (오른쪽) -->
    <div class="col-md-8">
      <!-- <h4 class="mb-3">회원 목록</h4> -->
      <table id="userTable" class="table table-bordered table-hover align-middle text-center">
        <thead class="table-primary">
          <tr>
            <th scope="col">선택</th>
            <th scope="col">이름</th>
            <th scope="col">전화번호</th>
            <th scope="col">이메일</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="user" items="${userList}">
            <input type="hidden" id="custSn" value="${user.custSn}">
            <tr 
              data-custsn="${user.custSn}"
              data-kornflnm="${user.kornFlnm}"
              data-mbltelno="${user.mblTelno}"
              data-emladdr="${user.emlAddr}"
              data-frstregdt="${user.frstRegDt}">
              <td><input type="checkbox" class="form-check-input"></td>
              <td>${user.kornFlnm}</td>
              <td>${user.mblTelno}</td>
              <td>${user.emlAddr}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>

<!-- 페이징 UI 영역 -->
<div class="container mt-4 text-center">
  <nav aria-label="Page navigation">
      <ul class="pagination justify-content-center">

          <!-- 이전 페이지 -->
          <c:if test="${pageInfo.hasPreviousPage}">
              <li class="page-item">
                  <a class="page-link" href="?pageNum=${pageInfo.prePage}&pageSize=${pageInfo.pageSize}" onclick="" aria-label="Previous">
                      <span aria-hidden="true">&laquo;</span>
                  </a>
              </li>
          </c:if>

          <!-- 숫자 페이지 -->
          <c:forEach begin="${pageInfo.navigateFirstPage}" end="${pageInfo.navigateLastPage}" var="i">
              <li class="page-item ${i == pageInfo.pageNum ? 'active' : ''}">
                  <a class="page-link" href="?pageNum=${i}&pageSize=${pageInfo.pageSize}" onclick="">${i}</a>
              </li>
          </c:forEach>

          <!-- 다음 페이지 -->
          <c:if test="${pageInfo.hasNextPage}">
              <li class="page-item">
                  <a class="page-link" href="?pageNum=${pageInfo.nextPage}&pageSize=${pageInfo.pageSize}" onclick="" aria-label="Next">
                      <span aria-hidden="true">&raquo;</span>
                  </a>
              </li>
          </c:if>

      </ul>
  </nav>
</div> 

    </div>
  </div>





</div>
 


<div class="container mt-4">
  <h4 class="mb-3 text-center">이달의 우수사원</h4>
  <div class="swiper mySwiper" style="width:350px; height:220px; margin:auto;">
    <div class="swiper-wrapper">
      <div class="swiper-slide d-flex flex-column align-items-center justify-content-center">
        <img src="../img/lioncry.jpg" class="rounded-circle mb-2" style="width:100px; height:100px; object-fit:cover;">
        <div class="fw-bold">홍길동</div>
        <div class="text-secondary">영업팀</div>
      </div>
      <div class="swiper-slide d-flex flex-column align-items-center justify-content-center">
        <img src="../img/lion.jpg" class="rounded-circle mb-2" style="width:100px; height:100px; object-fit:cover;">
        <div class="fw-bold">김철수</div>
        <div class="text-secondary">개발팀</div>
      </div>
      <!-- 추가 슬라이드 ... -->
    </div>
    <div class="swiper-pagination"></div>
    <div class="swiper-button-next"></div>
    <div class="swiper-button-prev"></div>
  </div>
</div>

<br><br>

</div>
</div>

	<script>

		
		function test() {

			
            console.log("test() Called");

            var kornFlnm = document.getElementById("kornFlnm").value;
            var mblTelno = document.getElementById("mblTelno").value;
            var emailAddr = document.getElementById("emailAddr").value;

				 $.ajax({
	                 async: false, //값을 리턴시 해당코드를 추가하여 동기로 변경 false : 동기, true : 비동기
	                 url: '/sampleJspMvc/ajax/saveProc',
	                 type: "post",

                     // dataType 옵션
                        /*
                        "json"	서버 응답을 JSON 객체로 파싱 (예: { "name": "홍길동" })
                        📌 JSON이 아닐 경우 parsererror 발생함!
                        "text"	응답을 문자열(String) 그대로 받아들임
                        예: "hello world" 또는 HTML 조각 등
                        "html"	응답을 HTML로 간주. 문자열로 받아서 DOM 삽입 등에 사용
                        "xml"	XML 응답을 DOM Document 객체로 파싱
                        "script"	응답 내용을 JavaScript로 간주하고 실행시킴 (보안상 주의)
                        "jsonp"	JSONP 요청으로 처리함 → 크로스도메인 API 호출 시 사용됨 (?callback=... 붙음)
                        "blob" / "arraybuffer"	jQuery 공식 dataType에는 없음. 이건 fetch API에서 사용됨.
                        "*", 생략	자동 추론 (서버의 Content-Type에 따라 jQuery가 자동 감지함)
                        */
	                 dataType: "text", // 서버에서 보내주는 데이터를 어떤 타입으로 받을건지

                     // 전송할 데이터
	                 data: {
	                	 kornFlnm : kornFlnm 
	                	 , mblTelno : mblTelno
	                	 , emailAddr : emailAddr
	                 },
	                 headers: {
	                     "Accept-Language": "ko-KR" // 한글을 지원하는 언어 코드로 설정
	                 },
	                 
	                 success: function(response) {
	                     console.log("ajax 통신 성공");
	                     console.log("서버 응답:", response.message);
	            
	                 },
	                 
	                 error: function(xhr, status, error) {
	                     console.log("ajax 통신 실패");
	                     console.error("에러 발생:", xhr, status, error);
	                     console.log("서버 응답 상세:", xhr.responseText);
	                 },
	                 
	                 complete : function(response) {
	                     console.log("완료후 로직 실행되는 로직");
	                     console.log(response.message);
                         location.reload(); // 성공 시 페이지 리로드
	                 	
	                 },
	                   // 추가: charset 옵션을 설정
	                 beforeSend: function(xhr) {
	                     xhr.overrideMimeType("text/plain; charset=utf-8");
	                 }
	             });
			}
			

	
	</script>
	<script>
	// 1. 전역에 선언
	let selectedCustSn = null;

	document.addEventListener('DOMContentLoaded', () => {
		  // 회원 목록 클릭 시 custSn 저장
		  document.querySelector('#userTable tbody').addEventListener('click', (e) => {
		    const tr = e.target.closest('tr');
		    if (!tr) return;
		    selectedCustSn = tr.dataset.custsn; // 여기서 할당

		    const { kornflnm, mbltelno, emladdr, frstregdt } = tr.dataset;

		    document.getElementById('detailKornFlnm').textContent = kornflnm || '';
		    document.getElementById('detailMblTelno').textContent = mbltelno || '';
		    document.getElementById('detailEmlAddr').textContent = emladdr || '';
		    document.getElementById('detailFrstRegDt').textContent = frstregdt || '';

		    document.getElementById('editKornFlnm').value = kornflnm || '';
		    document.getElementById('editMblTelno').value = mbltelno || '';
		    document.getElementById('editEmlAddr').value = emladdr || '';
		    document.getElementById('editFrstRegDt').value = frstregdt || '';
		  });

		  document.getElementById('editBtn').addEventListener('click', () => {
		    document.getElementById('detailView').style.display = 'none';
		    document.getElementById('editForm').style.display = '';
		    document.getElementById('editBtn').style.display = 'none';
		    document.getElementById('saveBtn').style.display = '';
		    document.getElementById('cancelBtn').style.display = '';
		  });

		  document.getElementById('cancelBtn').addEventListener('click', () => {
		    document.getElementById('detailView').style.display = '';
		    document.getElementById('editForm').style.display = 'none';
		    document.getElementById('editBtn').style.display = '';
		    document.getElementById('saveBtn').style.display = 'none';
		    document.getElementById('cancelBtn').style.display = 'none';
		  });

		  // 저장 버튼 클릭 시 custSn 사용
		  document.getElementById('saveBtn').addEventListener('click', () => {
		    if (!selectedCustSn) {
		      alert('수정할 회원을 선택하세요.');
		      return;
		    }

		    $.ajax({
		      url: '/sampleJspMvc/ajax/updateProc',
		      type: 'post',
		      dataType: 'text',
		      data: {
		        custSn: selectedCustSn,
		        kornFlnm: $('#editKornFlnm').val(),
		        mblTelno: $('#editMblTelno').val(),
		        emailAddr: $('#editEmlAddr').val()
		      },
		      success: function(response) {
		        alert('수정이 완료되었습니다.');
		        location.reload();
		      },
		      error: function() {
		        alert('수정 실패');
		      }
		    });
		  });

		  document.getElementById('deleteBtn').addEventListener('click', () => {
		    if (!selectedCustSn) {
		      alert('삭제할 회원을 선택하세요.');
		      return;
		    }
		    if (!confirm('정말 삭제하시겠습니까?')) return;

		    $.ajax({
		      url: '/sampleJspMvc/ajax/deleteProc',
		      type: 'delete',
		      dataType: 'text',
		      data: { custSn: selectedCustSn },
		      success: function(response) {
		        alert('삭제가 완료되었습니다.');
		        location.reload();
		      },
		      error: function() {
		        alert('삭제 실패');
		      }
		    });
		  });

		  document.getElementById('editPhoto').addEventListener('change', function(e) {
		    const file = e.target.files[0];
		    if (file) {
		      const reader = new FileReader();
		      reader.onload = function(ev) {
		        document.getElementById('detailPhoto').src = ev.target.result;
		      };
		      reader.readAsDataURL(file);
		    }
		  });


		});
	</script>
	
	<script>
		document.addEventListener('DOMContentLoaded', function () {
		  const swiper = new Swiper('.mySwiper', {
		    slidesPerView: 1,         // 슬라이드 1장씩
		    loop: true,               // 무한 반복
		    autoplay: {
		      delay: 3000,            // 3초마다 자동 전환
		      disableOnInteraction: false
		    },
		    pagination: {
		      el: '.swiper-pagination',
		      clickable: true
		    },
		    navigation: {
		      nextEl: '.swiper-button-next',
		      prevEl: '.swiper-button-prev'
		    }
		  });
		});
	</script>

</body>



	
</html>