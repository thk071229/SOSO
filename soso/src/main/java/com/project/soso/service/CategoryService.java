package com.project.soso.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.soso.dao.CategoryDao;
import com.project.soso.vo.CategoryVO;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryDao categoryDao;
	
	@Transactional
	public List<CategoryVO> getCategoryList(){
		return categoryDao.selectList();
	}
	
	// [수정] 파라미터를 int 하나가 아니라 List<Integer>로 받아야 합니다.
    @Transactional
    public void addAll(String accountId, List<Integer> categoryNoList) {
        
        // 사용자가 보낸 리스트([1, 3, 5])를 반복합니다.
        for(int categoryNo : categoryNoList) {
            // 하나씩 DB에 저장
            categoryDao.insert(accountId, categoryNo);
        }
    }
	

}
