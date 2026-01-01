package com.project.soso.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.soso.dao.AccountDao;
import com.project.soso.dto.AccountDto;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountRestController {
	
	@Autowired
	private AccountDao accountDao;
	
	
	@PostMapping("/join")
	public void join(@RequestBody AccountDto accountDto) {
		accountDao.insert(accountDto);
	}
}
