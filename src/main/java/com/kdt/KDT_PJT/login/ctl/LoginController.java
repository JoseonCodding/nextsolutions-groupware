package com.kdt.KDT_PJT.login.ctl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.login.svc.LoginService;

@RestController
@RequestMapping("/sampleReactMvc")
@CrossOrigin(origins = "http://localhost:3000") // @CrossOrigin 어노테이션으로 localhost:3000에 대해 교차 출처 허용
public class LoginController {


	
	@Autowired
	LoginService svc;

 
	   
	   /**
	    * CmmMap(공통Map)을 사용한 저장 예시
	   * @methodName    : saveUser
	   * @author        : 이의찬 / 대리
	   * @date          : 2025.07.23
	   * @param params
	   * @return
	   * Description     :
	    */
	   @PostMapping("/api/save")
	   public ResponseEntity<Map<String, String>> saveUser(@RequestBody CmmnMap params) {
	      
	       System.out.println("사번 : " + params.getString("employeeId"));
	       System.out.println("비밀번호 : " + params.getString("password"));	       
	       // 서비스호출 : 저장
	       
	       svc.saveProc(params);
	              
	       return ResponseEntity.ok(Map.of("message", "ok"));
	   }
	   
	   @PostMapping("/api/userlist")
	   public List<CmmnMap> getUserList() {
		   
		   return svc.getUserList();
	   }
	   
	   @PostMapping("/api/updateuser")
	   public void updateUser(@RequestBody CmmnMap params) {
		   
		   System.out.println("col1 " + params.getString("col1"));
		   System.out.println("col2 " + params.getString("col2"));
		   
		   svc.updateUser(params);
	   }
	   


}
