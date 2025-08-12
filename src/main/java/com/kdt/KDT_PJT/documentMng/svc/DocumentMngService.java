package com.kdt.KDT_PJT.documentMng.svc;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.KDT_PJT.documentMng.mapper.DocumentMngMapper;
import com.kdt.KDT_PJT.documentMng.model.DocumentMngDTO;
import com.kdt.KDT_PJT.documentMng.model.DocumentVersionSummaryDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentMngService {

    private final DocumentMngMapper mapper;

    public List<DocumentVersionSummaryDTO> getVersionList(Long originalId) {
        return mapper.selectVersionListByOriginalId(originalId);
    }

    public DocumentMngDTO getVersionDetail(Long versionId) {
        return mapper.selectVersionById(versionId);
    }
    
    
	/*
	 * // 작업중입니다. (필규)
	 * 
	 * @Transactional public void restoreVersion(Long versionId) { // 1. 원본 테이블 덮어쓰기
	 * mapper.restoreVersion(versionId);
	 * 
	 * // 2. originalId 가져오기 Long originalId =
	 * mapper.getOriginalIdByVersionId(versionId); if (originalId == null) { throw
	 * new IllegalStateException("해당 versionId의 originalId를 찾을 수 없습니다."); }
	 * 
	 * // 3. 다음 버전명 생성 String nextVersionName =
	 * getNextVersionName(originalId.intValue());
	 * 
	 * // 4. 현재 원본 테이블 상태 백업 backupProjectVersion(originalId.intValue(),
	 * nextVersionName); }
	 */

}
