package com.kdt.KDT_PJT.pjt_mng.ctl;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.pjt_mng.svc.ProjcetMngService;

@Controller
@RequestMapping("/pjtMng")
public class ProjectMngController {
	
	@Autowired
	ProjcetMngService projcetMngService;
	
	// log 사용을 위함
	private final Logger log = LoggerFactory.getLogger(getClass());
	

	@GetMapping("/getPjtList")
	public String getPjtList(Model model) {
		
		log.info("getPjtList Called >>> ");
		
		
		List<CmmnMap> pjtList = projcetMngService.getPjtList();
		
		model.addAttribute("pjtList", pjtList);
		
		return "pjt_mng/pjt_main";
	}
	
	
	
	@GetMapping("/pjtDetail")
	public String getPjtDetail(@RequestParam("pjtSn") String pjtSn, Model model) {
	    log.info("getPjtDetail Called >>> " + pjtSn);
	    
	    CmmnMap pjtDetail = projcetMngService.getPjtDetail(pjtSn);
	    model.addAttribute("pjt", pjtDetail);

	    return "pjt_mng/pjt_detail";
	}


	@GetMapping("/getSavePjtForm")
	public String getSavePjtForm(Model model) {
		
		
		return "pjt_mng/pjt_reg_form";

	}	
	

	@PostMapping("/savePjtProc")
	public String savePjtProc(
			Model model
			,@RequestParam("pjtNm") String pjtNm,
		    @RequestParam(value = "empNm", required = false) String empNm,
		    @RequestParam(value = "pjtBgngDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtBgngDt,
		    @RequestParam(value = "pjtEndDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtEndDt
			) {
		
		log.info("savePjtProc Called >>> ");
		
		CmmnMap params = new CmmnMap();
		log.info("pjtNm " + pjtNm);
		log.info("empNm" + empNm);
		log.info("pjtBgngDt " + pjtBgngDt);
		log.info("pjtEndDt " + pjtEndDt);
		
		
		params.put("PJT_NM", pjtNm);
		params.put("EMP_NM", empNm);
		params.put("PJT_BGNG_DT", pjtBgngDt);
		params.put("PJT_END_DT", pjtEndDt);
		
		
		projcetMngService.savePjtProc(params);
		
		
		return "redirect:/pjtMng/getPjtList";

	}	
}
