package com.project.soso.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.soso.service.RegionService;
import com.project.soso.vo.RegionVO;

@CrossOrigin
@RestController
@RequestMapping("/region")
public class RegionRestController {
	
	@Autowired
	private RegionService regionService;
	
	@GetMapping("/")
	public List<RegionVO> selectList(){
		return regionService.getRegionList();
	}
	

}
