function openEdit(btn) {
  const dlg   = document.getElementById('editDialog');
  const id    = btn.getAttribute('data-id');
  const name  = btn.getAttribute('data-name');
  const rolesCsv = (btn.getAttribute('data-roles') || '')
                    .split(',')
                    .map(s => s.trim())
                    .filter(Boolean);
  const cmt   = btn.getAttribute('data-cmt') === 'true';
  const like  = btn.getAttribute('data-like') === 'true';

  document.getElementById('edit-boardId').value   = id;
  document.getElementById('edit-boardName').value = name;

  const roleUser  = document.getElementById('edit-role-user');
  const roleAdmin = document.getElementById('edit-role-admin');
  const roleNm    = document.getElementById('edit-role-nm');

  [roleUser, roleAdmin, roleNm].forEach(el => el && (el.checked = false));

  rolesCsv.forEach(r => {
    if (r === 'USER'           && roleUser)  roleUser.checked  = true;
    if (r === 'ADMIN'          && roleAdmin) roleAdmin.checked = true;
    if (r === 'NOTICE_MANAGER' && roleNm)    roleNm.checked    = true;
  });

  document.getElementById('edit-useComment').checked = cmt;
  document.getElementById('edit-useLike').checked    = like;

  if (!dlg.open) dlg.showModal();
}

document.addEventListener("DOMContentLoaded", function() {
  
  $(".aaa").on("change", function(){
	
	let count = $(".aaa:checked").length;
	
	//    alert("체크된 개수: " + count);
	if(count ==0){
		alert("최소 1개 이상 선택 필요");
		$(this).prop("checked", true); 
	}
	
  });
  
  
  $(".bbb").on("change", function(){
  	
  	let count = $(".bbb:checked").length;
  	
  	//    alert("체크된 개수: " + count);
  	if(count ==0){
  		alert("최소 1개 이상 선택 필요");
  		$(this).prop("checked", true); 
  	}
  	
    });
  
});

(function () {
    const el = document.getElementById('errBox');
    if (!el) return;                 // 에러 없으면 아무 것도 안 함
    alert(el.dataset.msg);           // alert만 띄움
    el.remove();                     // (선택) DOM 정리
  })();

  // ===== 오늘 통계 =====
const USE_FETCH = true; // 서버에서 최신 통계 호출. 초기 todayStats만 쓰려면 false로

async function fetchToday(boardId) {
  const res = await fetch(`/admin/boards/stats/today/${boardId}`, { cache: 'no-store' });
  if (!res.ok) throw new Error('통계 요청 실패');
  return res.json(); // { viewToday, likeToday, postToday } 형태 기대
}

function setActive(el) {
  document.querySelectorAll('#stat-board-list .row.active').forEach(li => li.classList.remove('active'));
  el.classList.add('active');
}

function updateKPI(name, views, likes, posts) {
  const fmt = n => new Intl.NumberFormat().format(Number(n) || 0);

  // 보드명(조회수/좋아요 라벨 옆 둘 다 지원)
  //const nm1 = document.getElementById('kpi-boardname');
 // const nm2 = document.getElementById('kpi-boardname-dup');
  //if (nm1) nm1.textContent = name ? `(${name})` : '';
  //if (nm2) nm2.textContent = name ? `(${name})` : '';

  // 값 반영
  const vEl = document.getElementById('kpi-views');
  const lEl = document.getElementById('kpi-likes');
  const pEl = document.getElementById('kpi-posts');
  if (vEl) vEl.textContent = fmt(views);
  if (lEl) lEl.textContent = fmt(likes);
  if (pEl) pEl.textContent = fmt(posts);
}

async function loadToday(liEl) {
  if (!liEl) return;
  setActive(liEl);

  const id   = liEl.getAttribute('data-id');
  const name = liEl.getAttribute('data-name') || liEl.querySelector('span')?.textContent || '';

  if (USE_FETCH) {
    try {
      const data = await fetchToday(id);
      // 서버가 숫자 아닌 값을 줄 경우 대비
      const v = Number(data?.viewToday ?? liEl.getAttribute('data-views') ?? 0);
      const l = Number(data?.likeToday ?? liEl.getAttribute('data-likes') ?? 0);
	  const p = Number(data?.postToday ?? liEl.getAttribute('data-posts') ?? 0);
      updateKPI(name, v, l, p);
      return;
    } catch (e) {
      console.error('[todayStats] fetch 실패, data-*로 폴백:', e);
    }
  }

  // 폴백: 서버 호출 끄거나 실패 시, 리스트의 data-* 사용
  const views = Number(liEl.getAttribute('data-views') || 0);
  const likes = Number(liEl.getAttribute('data-likes') || 0);
  const posts = Number(liEl.getAttribute('data-posts') || 0);
  updateKPI(name, views, likes, posts);
}

// 초기 로드: 리스트 첫 항목 선택
(function initTodayStats() {
  const first = document.querySelector('#stat-board-list .row');
  if (first) loadToday(first);
})();

