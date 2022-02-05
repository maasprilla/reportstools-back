package co.com.reportstools.controllers;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public String getStoreProcedureInfo(@RequestParam(required = false) String proyecto,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(required = false) String sortFilter, @RequestParam(required = false) String dataFilter) {

		return legalizacionService.getStoreProcedureInfo(proyecto, page, size, sortFilter, dataFilter);
	}

	@RequestMapping(value = "/legalizacion/filter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List getStoreProcedureHeaderOptionList(@RequestParam String proyecto,
			@RequestParam(required = false) String dataFilter, @RequestParam String dataGroup) {

		return legalizacionService.getStoreProcedureHeaderOptionList(proyecto, dataFilter, dataGroup);
	}

	@RequestMapping(value = "/legalizacion/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String UpdateData(@RequestBody String peticion) {
		peticion = peticion.replace("Invalid Date", "null");
		peticion = peticion.replace("''null''", "NULL");
		System.out.println(peticion);
		return legalizacionService.updateLegalizacion(peticion);
	}

	@RequestMapping(value = "/legalizacion/multisave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String multipleUpdateLegalizacion(@RequestBody String peticion) {
		JSONObject jsonTemp = new JSONObject(peticion);
		String field = !jsonTemp.isNull("field") ? jsonTemp.getString("field") : null;
		String newValue = !jsonTemp.isNull("newValue") ? jsonTemp.getString("newValue") : null;
		String obras = !jsonTemp.isNull("obras") ? jsonTemp.getString("obras") : null;
		String sortFilter = !jsonTemp.isNull("sortFilter") ? jsonTemp.getString("sortFilter") : null;
		String dataFilter = !jsonTemp.isNull("dataFilter") ? jsonTemp.getString("dataFilter") : null;

		return legalizacionService.multipleUpdateLegalizacion(field, newValue, obras,sortFilter, dataFilter);
	}
}
