package com.kdt.KDT_PJT.sample.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kdt.KDT_PJT.sample.vo.SampleVO;

@Repository
public class SampleDao extends SqlSessionDaoSupport {

	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	};

	public <T> T selectOne(String queryId) {
		return getSqlSession().selectOne(queryId);
	}
	
	public int insert(String queryId, Object parameterObject) {
		return getSqlSession().insert(queryId, parameterObject);
	}

	
	/**
	 * 리스트 조회 처리 SQL mapping 을 실행한다.
	 *
	 * @param queryId - 리스트 조회 처리 SQL mapping 쿼리 ID
	 *
	 * @return 결과 List 객체 - SQL mapping 파일에서 지정한  resultType/resultMap 에 의한 결과 객체(보통 VO 또는 Map)의 List
	 */
	public <E> List<E> selectList(String queryId) {
		return getSqlSession().selectList(queryId);
	}

	
	/**
	 * 리스트 조회 처리 SQL mapping 을 실행한다.
	 *
	 * @param queryId - 리스트 조회 처리 SQL mapping 쿼리 ID
	 * @param parameterObject - 리스트 조회 처리 SQL mapping 입력 데이터(조회 조건)를 세팅한 파라메터 객체(보통 VO 또는 Map)
	 *
	 * @return 결과 List 객체 - SQL mapping 파일에서 지정한  resultType/resultMap 에 의한 결과 객체(보통 VO 또는 Map)의 List
	 */
	public <E> List<E> selectList(String queryId, Object parameterObject) {
		return getSqlSession().selectList(queryId, parameterObject);
	}


	public int update(String queryId, Object parameterObject) {
		return getSqlSession().update(queryId, parameterObject);
	}

	public int delete(String queryId, Object parameterObject) {
		return getSqlSession().delete(queryId, parameterObject);
	}
}
