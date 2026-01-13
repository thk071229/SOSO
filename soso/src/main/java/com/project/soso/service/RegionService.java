package com.project.soso.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.soso.dao.RegionDao;
import com.project.soso.vo.RegionVO;

import jakarta.annotation.PostConstruct;

@Service
public class RegionService {
	
	@Autowired
	private RegionDao regionDao;
	
	// 메모리 캐시 저장소
	private List<RegionVO> cachedRegions;
	
	// 서버가 시작될 때 딱 1번 자동 실행
	@PostConstruct
	public void init() {
		
		System.out.println("🌏 [RegionService] 지역 데이터 캐싱 시작...");
		
		// DB에서 모든 지역 정보를 가져옴
		this.cachedRegions = regionDao.selectList();
		
		// 로그 확인
		if(this.cachedRegions != null) {
			System.out.println("✅ [RegionService] 캐싱 완료! 총 개수: " + this.cachedRegions.size());
		}
		else {
			System.out.println("❌ [RegionService] 데이터가 없습니다. DB를 확인해주세요.");
		}
	}
	
	public List<RegionVO> getRegionList(){
		// 서버 시작 때 오류가 발생해 데이터가 없으면 가져온다
		if(this.cachedRegions == null || this.cachedRegions.isEmpty()) {
			System.out.println("⚠️ 캐시가 비어있어서 DB에서 다시 조회합니다.");
            this.cachedRegions = regionDao.selectList();
		}
		// DB 안 거치고 메모리에 있는 걸 바로 줍니다
        return this.cachedRegions;
	}
	
	// 회원의 지역정보 등록(여기서 있으면 삭제 후 등록)
	@Transactional
	public void insert(String accountId, int regionNo, String regionType) {
		// 존재여부 확인
		int result = regionDao.findRegionType(accountId, regionType);
		
		// 있다면 삭제
		if(result > 0) {
			regionDao.delete(accountId, regionType);
		}
		
		// 등록
		regionDao.insert(accountId, regionNo, regionType);
	}

}
