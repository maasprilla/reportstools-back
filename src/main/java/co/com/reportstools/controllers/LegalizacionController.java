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
	public String getStoreProcedureInfo(@RequestBody String peticion) {

		JSONObject jsonTemp = new JSONObject(peticion);
		Integer reportType = !jsonTemp.isNull("reportType") ? jsonTemp.getInt("reportType") : null;
		String proyecto = !jsonTemp.isNull("proyecto") ? jsonTemp.getString("proyecto") : null;
		Integer page = !jsonTemp.isNull("page") ? jsonTemp.getInt("page") : 1;
		Integer size = !jsonTemp.isNull("size") ? jsonTemp.getInt("size") : 5;
		String sortFilter = !jsonTemp.isNull("sortFilter") ? jsonTemp.getString("sortFilter") : null;
		String dataFilter = !jsonTemp.isNull("dataFilter") ? jsonTemp.getString("dataFilter") : null;

		return legalizacionService.getStoreProcedureInfo(proyecto, page, size, sortFilter, dataFilter, reportType);
	}

	@RequestMapping(value = "/legalizacion/filter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List getStoreProcedureHeaderOptionList(@RequestBody String peticion) {

		JSONObject jsonTemp = new JSONObject(peticion);
		Integer reportType = !jsonTemp.isNull("reportType") ? jsonTemp.getInt("reportType") : null;
		String proyecto = !jsonTemp.isNull("proyecto") ? jsonTemp.getString("proyecto") : null;
		String dataGroup = !jsonTemp.isNull("dataGroup") ? jsonTemp.getString("dataGroup") : null;
		String dataFilter = !jsonTemp.isNull("dataFilter") ? jsonTemp.getString("dataFilter") : null;

		return legalizacionService.getStoreProcedureHeaderOptionList(proyecto, dataFilter, dataGroup, reportType);

	}

	@RequestMapping(value = "/legalizacion/filter/like", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List getStoreProcedureHeaderOptionFilterList(@RequestBody String peticion) {

		JSONObject jsonTemp = new JSONObject(peticion);
		Integer reportType = !jsonTemp.isNull("reportType") ? jsonTemp.getInt("reportType") : null;
		String proyecto = !jsonTemp.isNull("proyecto") ? jsonTemp.getString("proyecto") : null;
		String dataGroup = !jsonTemp.isNull("dataGroup") ? jsonTemp.getString("dataGroup") : null;
		String dataFilter = !jsonTemp.isNull("dataFilter") ? jsonTemp.getString("dataFilter") : null;
		String filterLike = !jsonTemp.isNull("filterLike") ? jsonTemp.getString("filterLike") : null;

		return legalizacionService.getStoreProcedureHeaderOptionFilterList(proyecto, dataFilter, dataGroup, filterLike,
				reportType);

	}

	@RequestMapping(value = "/legalizacion/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String UpdateData(@RequestBody String peticion) {
		JSONObject jsonTemp = new JSONObject(peticion);
		Integer reportType = !jsonTemp.isNull("reportType") ? jsonTemp.getInt("reportType") : null;
		String data = !jsonTemp.isNull("data") ? jsonTemp.getString("data") : null;

		data = data.replace("Invalid Date", "null");
		data = data.replace("''null''", "NULL");
		System.out.println(data);
		return legalizacionService.updateLegalizacion(data, reportType);
	}

	@RequestMapping(value = "/legalizacion/multisave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String multipleUpdateLegalizacion(@RequestBody String peticion) {
		JSONObject jsonTemp = new JSONObject(peticion);
		Integer reportType = !jsonTemp.isNull("reportType") ? jsonTemp.getInt("reportType") : null;
		String field = !jsonTemp.isNull("field") ? jsonTemp.getString("field") : null;
		String newValue = !jsonTemp.isNull("newValue") ? jsonTemp.getString("newValue") : null;
		String obras = !jsonTemp.isNull("obras") ? jsonTemp.getString("obras") : null;
		String sortFilter = !jsonTemp.isNull("sortFilter") ? jsonTemp.getString("sortFilter") : null;
		String dataFilter = !jsonTemp.isNull("dataFilter") ? jsonTemp.getString("dataFilter") : null;

		return legalizacionService.multipleUpdateLegalizacion(field, newValue, obras, sortFilter, dataFilter,
				reportType);
	}
}
