package com.kdt.KDT_PJT.pjt_mng.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kdt.KDT_PJT.cmmn.dao.CmmnDao;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper;

import lombok.RequiredArgsConstructor;



@Service
public class ProjectMngService  {

    @Autowired
    private CmmnDao cmmnDao;
    
    @Autowired
    private PjtMngMapper pjtMngMapper;
    
	// log 사용을 위함
	private final Logger log = LoggerFactory.getLogger(getClass());

    // 🔹 프로젝트 전체 조회 (기존)
    public List<CmmnMap> getPjtList() {
        return cmmnDao.selectList("com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.getPjtList");
    }

    // 🔹 프로젝트 상세 조회 (기존)
    public CmmnMap getPjtDetail(String pjtSn) {
    	
    	
    	String queryId = "com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.getPjtDetail";
    	
        return cmmnDao.selectOne(queryId, pjtSn);
    }

    // 🔹 프로젝트 등록 (기존)
    public void savePjtProc(CmmnMap params) {
    	
    	String queryId = "com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.savePjtProc";
    	
        cmmnDao.insert(queryId, params);
    }

    // 🔹 프로젝트 수정 (기존)
    public void updatePjtProc(CmmnMap params) {
    	
    	log.info("svc updatePjtProc >>> ");
    	
        cmmnDao.update("com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.updatePjtProc", params);
    }

    // 🔹 키워드 검색 (기존)
    public List<CmmnMap> searchProjectMngList(String keyword) {
        return cmmnDao.selectList("com.kdt.mapper.pjt_mng.PjtMngMapper.searchProjectMngList", keyword);
    }

    
    
    // ✅ ✅ ✅ 정렬 + 페이징 + 검색 (신규)
    public List<CmmnMap> searchProjectPagedList(String keyword, String sortType, String order, int offset, int pageSize) {
        Map<String, Object> param = new HashMap<>();
        param.put("keyword", keyword);
        param.put("sortType", sortType);
        param.put("order", order);
        param.put("offset", offset);
        param.put("pageSize", pageSize);

        return cmmnDao.selectList("com.kdt.mapper.pjt_mng.PjtMngMapper.searchProjectPagedList", param);
    }


    
    // ✅ 전체 카운트 조회 (신규)
    public int countProjectList(String keyword) {
        return cmmnDao.selectOne("com.kdt.mapper.pjt_mng.PjtMngMapper.countProjectList", keyword);
    }

	public PageInfo<CmmnMap> getProjectList(int pageNum, int pageSize, String keyword) {
		
       PageHelper.startPage(pageNum, pageSize);

       // ② 페이징 걸린 상태로 select 실행
       List<CmmnMap> list = cmmnDao.selectList("com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.searchProjectMngList", keyword);

       // ③ PageInfo로 래핑
       return new PageInfo<>(list);

	}

	
	@Service
	@RequiredArgsConstructor
	public class ProjcetMngService {
		  private final ProjectMngMapper projectMngMapper;   // ✅ 프로젝트 INSERT/UPDATE용 매퍼
		   private final ProjectFileMapper projectFileMapper; // (파일 저장 로직에서 사용)
		    private final FileStorageHelper fileStorageHelper; // (파일 저장 헬퍼)
	
   // ✅ 2) 신규 저장 시 생성된 PJT_SN 을 반드시 회수해서 반환
	public int saveOrUpdateProject(CmmnMap form) {
		Object pjtSnObj = form.get("PJT_SN");
        if (pjtSnObj != null) {
            try {
                int pjtSn = Integer.parseInt(String.valueOf(pjtSnObj));
                if (pjtSn > 0) {
                    // TODO: 필요하면 수정 로직 작성
                    // form.put("LAST_MDFR_ID", loginId 등);
                    // form.put("LAST_MDFCN_DT", now());
                    // projectMngMapper.updateProject(form);
                    return pjtSn;
                }
            } catch (NumberFormatException ignore) {}
        }
     // 신규 등록: 등록/수정 메타 세팅
        String now = now();
        form.put("FRST_RGTR_ID", "SYSTEM");
        form.put("FRST_REG_DT",  now);
        form.put("LAST_MDFR_ID", "SYSTEM");
        form.put("LAST_MDFCN_DT", now);
        form.putIfAbsent("USE_YN", "Y");

        // ⬇️ XML에서 useGeneratedKeys 로 PJT_SN 이 form에 채워짐 (id=savePjtProc)
        projectMngMapper.savePjtProc(form);

        Integer newSn = form.getInt("PJT_SN"); // 🔥 여기서 생성된 PK를 받는다
        if (newSn == null || newSn == 0) {
            throw new IllegalStateException("프로젝트 PK 생성값을 받지 못했습니다.");
        }
        return newSn;
			}
	// (참고) 파일 저장 메서드는 기존 그대로 사용
    @Transactional
    public void saveProjectFiles(int pjtSn, MultipartFile[] files, String rgtrId) {
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            var saved = fileStorageHelper.store(pjtSn, file);

            Map<String,Object> row = new HashMap<>();
            row.put("ATCH_FILE_PATH_NM", saved.getPath());
            row.put("ATCH_FILE_ORGNL_NM", saved.getOriginal());
            row.put("ATCH_FILE_NM", saved.getStored());
            row.put("USE_YN", "Y");
            row.put("FRST_RGTR_ID", rgtrId);
            row.put("FRST_REG_DT", now());
            row.put("LAST_MDFR_ID", rgtrId);
            row.put("LAST_MDFCN_DT", now());
            projectFileMapper.insertAtchFileBasc(row);

            int atchFileSn = (int) row.get("ATCH_FILE_SN");

            Map<String,Object> link = new HashMap<>();
            link.put("PJT_SN", pjtSn);
            link.put("ATCH_FILE_SN", atchFileSn);
            link.put("FRST_RGTR_ID", rgtrId);
            link.put("FRST_REG_DT", now());
            projectFileMapper.insertPjtFileMapping(link);
        }
    }

    private String now() {
        return java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
	   
		public List<CmmnMap> getApproverList() {
			// TODO Auto-generated method stub
			return pjtMngMapper.getApproverList();	
			}

	    

		public String now() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public int saveOrUpdateProject(CmmnMap form) {
			// TODO Auto-generated method stub
			return 0;
		}

		public void saveProjectFiles(int pjtSn, MultipartFile[] files, Object object) {
			// TODO Auto-generated method stub
			
		}

	
}
	
	

	
    
    

    


