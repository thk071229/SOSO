package com.project.soso.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.soso.dto.AccountCategoryDto;
import com.project.soso.service.CategoryService;
import com.project.soso.vo.CategoryVO;
import com.project.soso.vo.TokenVO;

@CrossOrigin
@RestController
@RequestMapping("/category")
public class CategoryRestController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/")
	public List<CategoryVO> selectList(){
		return categoryService.getCategoryList();
	}
	
	@PostMapping("/insert")
	public void insert(
			@RequestAttribute TokenVO tokenVO,
			@RequestBody AccountCategoryDto accountCategoryDto){
		categoryService.addAll(tokenVO.getLoginId(), accountCategoryDto.getCategoryList());
	}
	

}
