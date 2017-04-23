package com.demo.api.controller;

import com.demo.api.entity.EEntity;
import com.demo.api.repository.EEntityRepository;
import com.demo.api.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
public class WebController {

	private static final Logger logger = LoggerFactory.getLogger(WebController.class);


	@Autowired
	private EEntityRepository eEntityRepository;

	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {
		return "index";
	}

	@RequestMapping("/settings")
	public String settings(Map<String, Object> model) {
		return "settings";
	}

	@PostMapping("/del")
	@ResponseBody
	public String delete(String ids) {
		Map<String, Object> map = new HashMap<>();
		int c = 0;
		if(ids != null && ids != "" && ids.length() > 0) {
			String[] ss = ids.split(",");
			for(String id : ss) {
				EEntity e = eEntityRepository.findOne(id);
				if(e != null) {
					c++;
					eEntityRepository.delete(e);
				}
			}
		}
		if(c > 0) {
			map.put("result", 1);
		} else {
			map.put("result", 0);
		}
		return JsonUtil.objectToJson(map);
	}
}