package com.project.soso.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.soso.service.RegionService;
import com.project.soso.vo.RegionVO;
import com.project.soso.vo.TokenVO;

@CrossOrigin
@RestController
@RequestMapping("/region")
public class RegionRestController {
	
	@Autowired
	private RegionService regionService;
	
	@GetMapping("")
	public List<RegionVO> selectList(){
		return regionService.getRegionList();
	}
	
	@PostMapping("/insert")
	public void insert(
			@RequestAttribute TokenVO tokenVO,
			@RequestParam int regionNo, 
			@RequestParam String regionType) {
		regionService.insert(tokenVO.getLoginId(), regionNo, regionType);
	}
	

}
