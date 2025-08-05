<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.14/index.global.min.js"></script>
</head>
<body>
	<div id="calendar"></div>
</body>
<script type="text/javascript">
document.addEventListener('DOMContentLoaded', function() {
	let calendarEl = document.getElementById('calendar');
	
	let headerToolbar = {
		left: 'prevYear,prev,next,nextYear today',
		center: 'title',
		right: 'dayGridMonth,dayGridWeek,timeGridDay'
	}
	
	let calendar = new FullCalendar.Calendar(calendarEl, {
		initialView: 'dayGridMonth',
		headerToolbar: headerToolbar
	});
	calendar.render();
});
</script>
</html>