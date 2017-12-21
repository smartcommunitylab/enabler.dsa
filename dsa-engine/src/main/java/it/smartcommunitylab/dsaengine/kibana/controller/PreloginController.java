package it.smartcommunitylab.dsaengine.kibana.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PreloginController {

	@GetMapping("/prelogin")
	public String prelogin(HttpServletRequest request, HttpServletResponse response) {
		return "prelogin";
	}

}
