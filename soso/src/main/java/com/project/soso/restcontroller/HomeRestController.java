package com.project.soso.restcontroller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class HomeRestController {
	
	@GetMapping("/")
	public String home() {
		return "서버 정상 작동중";
	}

}
