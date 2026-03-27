package com.kdt.KDT_PJT.approval.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kdt.KDT_PJT.approval.model.ApprovalDocDTO;
import com.kdt.KDT_PJT.approval.model.ApprovalTemplateDTO;

@Mapper
public interface ApprovalTemplateMapper {

    // 템플릿 관련
    List<ApprovalTemplateDTO> selectTemplateList(@Param("companyId") Integer companyId);
    ApprovalTemplateDTO selectTemplateById(@Param("templateId") Integer templateId);
    void insertTemplate(ApprovalTemplateDTO dto);
    void updateTemplate(ApprovalTemplateDTO dto);
    void deleteTemplate(@Param("templateId") Integer templateId, @Param("companyId") Integer companyId);

    // 자유양식 결재 문서 관련
    List<ApprovalDocDTO> selectDocList(
            @Param("companyId") Integer companyId,
            @Param("writerId") String writerId,
            @Param("isAdmin") boolean isAdmin);
    ApprovalDocDTO selectDocById(@Param("docId") Integer docId, @Param("companyId") Integer companyId);
    void insertDoc(ApprovalDocDTO dto);
    void updateDocStatus(
            @Param("docId") Integer docId,
            @Param("status") String status,
            @Param("approverId") String approverId,
            @Param("approverName") String approverName,
            @Param("rejectReason") String rejectReason);
    void softDeleteDoc(@Param("docId") Integer docId, @Param("writerId") String writerId);
}
