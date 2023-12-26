package edu.uclm.esi.tysweb2023.http;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("ejemplos")
public class EjemplosController {

	@GetMapping("/start")
	public String start(HttpServletRequest request,@RequestParam(required=false) String name) {
		Enumeration<String> headers=request.getHeaderNames();
		
		while (headers.hasMoreElements()) {
			String header=headers.nextElement();
			System.out.println(header+" = "+ request.getHeader(header));
		}
		return "Hola"+name;
	}
	
}
