<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>★전자결재 메인 (CKEditor 5 사용)</title>
    <!-- CKEditor 5 CDN 추가 -->
    <script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>
</head>
<body>
	<h1>★전자결재 메인 (CKEditor 5 사용) (Spring_work 테스트2)</h1>
    <form action="SaveContentServlet" method="POST">
        <input type="text" name="title" placeholder="제목을 입력하세요" required>
        <textarea id="editor" name="content"></textarea>
        <button type="submit">저장</button>
    </form>
    <script>
        ClassicEditor
            .create(document.querySelector('#editor'))
            .catch(error => console.error('Editor error:', error));
    </script>
</body>
</html>
