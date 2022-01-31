package co.com.reportstools.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.reportstools.services.LegalizacionService;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class LegalizacionController {

	@Autowired
	private LegalizacionService legalizacionService;

	@RequestMapping(value = "/legalizacion", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getStoreProcedureInfo(
			@RequestParam(required = false) String proyecto,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "5") int size
			) {
		return legalizacionService.getStoreProcedureInfo(page,size);
	}
}
