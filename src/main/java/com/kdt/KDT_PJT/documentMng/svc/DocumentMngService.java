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

    @Transactional
    public void restoreVersion(Long versionId) {
        mapper.restoreVersion(versionId);
    }

}
