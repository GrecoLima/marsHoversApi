package com.marshoversapi.web;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marshoversapi.response.MarsRoverApiResponse;
import com.marshoversapi.service.MarsRoverApiService;
import com.marshoversapi.vo.HomeVO;

@Controller
public class HomeController {

	@Autowired
	private MarsRoverApiService roverService;

	@GetMapping("/")
	public String getHomeView(ModelMap model, Long userId, Boolean createUser)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		HomeVO homeVO = createDefaultHomeVO(userId);

		if (Boolean.TRUE.equals(createUser) && userId == null) {
			homeVO = roverService.save(homeVO);
		} else {
			homeVO = roverService.findByUserId(userId);
			if (homeVO == null) {
				homeVO = createDefaultHomeVO(userId);
			}
		}

		MarsRoverApiResponse roverData = roverService.getRoverData(homeVO);
		model.put("roverData", roverData);
		model.put("homeVO", homeVO);
		model.put("validCameras", roverService.getValidCameras().get(homeVO.getMarsApiRoverData()));
		if (!Boolean.TRUE.equals(homeVO.getRememberPreferences()) && userId != null) {
			HomeVO defaultHomeVO = createDefaultHomeVO(userId);
			roverService.save(defaultHomeVO);
		}

		return "index";
	}

	@GetMapping("/savedPreferences")
	@ResponseBody
	public HomeVO getSavedPreferences(Long userId) {
		if (userId != null)
			return roverService.findByUserId(userId);
		else
			return createDefaultHomeVO(userId);
	}

	private HomeVO createDefaultHomeVO(Long userId) {
		HomeVO homeVO = new HomeVO();
		homeVO.setMarsApiRoverData("Opportunity");
		homeVO.setMarsSol(1);
		homeVO.setUserId(userId);
		return homeVO;
	}

	@PostMapping("/")
	public String postHomeView(HomeVO homeVO) {
		homeVO = roverService.save(homeVO);
		return "redirect:/?userId=" + homeVO.getUserId();
	}

}
