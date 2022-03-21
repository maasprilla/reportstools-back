package co.com.reportstools.controllers;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.reportstools.repositories.GeneralRepository;
import co.com.reportstools.services.AuditService;
import co.com.reportstools.services.GeneralService;
import co.com.reportstools.services.LdapUserServiceImpl;
import co.com.reportstools.services.LegalizacionService;
import co.com.reportstools.services.UserService;
import co.com.reportstools.utils.JwtTokenUtil;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class LegalizacionController {

	@Autowired
	private LegalizacionService legalizacionService;

	@Autowired
	private GeneralService generalService;

	@Autowired
	private UserService userService;

	@Autowired
	private AuditService auditService;

	@Autowired
	private LdapUserServiceImpl ldapUserServiceImpl;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> login(@RequestBody String peticion) {
		JSONObject jsonTemp = new JSONObject(peticion);
		String user = !jsonTemp.isNull("user") ? jsonTemp.getString("user") : null;
		String password = !jsonTemp.isNull("password") ? jsonTemp.getString("password") : null;
//		return ldapUserServiceImpl.authenticate("RecaudosCartera", "Recaudos2019");
		return ldapUserServiceImpl.authenticate(user, password);

	}

	@RequestMapping(value = "/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getUsers(@RequestBody String peticion) {

		return userService.getUsers();
	}

	@RequestMapping(value = "/roles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getRoles(@RequestBody String peticion) {

		return userService.getRoles();
	}

	@RequestMapping(value = "/update-role", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String addOrUpdateRole(@RequestBody String peticion) {
		JSONObject jsonTemp = new JSONObject(peticion);
		String userId = !jsonTemp.isNull("userId") ? jsonTemp.getString("userId") : null;
		String roleId = !jsonTemp.isNull("roleId") ? jsonTemp.getString("roleId") : null;
		return userService.addOrUpdateRole(userId, roleId);
	}

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
	public String UpdateData(HttpServletRequest request, @RequestBody String peticion) {
		JSONObject jsonTemp = new JSONObject(peticion);
		Integer reportType = !jsonTemp.isNull("reportType") ? jsonTemp.getInt("reportType") : null;
		String data = !jsonTemp.isNull("data") ? jsonTemp.getString("data") : null;
		List auditList = !jsonTemp.isNull("auditList") ? jsonTemp.getJSONArray("auditList").toList()
				: new ArrayList<>();

		data = data.replace("Invalid Date", "null");
		data = data.replace("''null''", "NULL");
		System.out.println(data);

		String authorization = request.getHeader("Authorization");
		String user = null;
		if (authorization != null && authorization != "") {
			user = jwtTokenUtil.getUsernameFromToken(authorization);
		}

		return legalizacionService.updateLegalizacion(data, reportType, auditList, user);
	}

	@RequestMapping(value = "/legalizacion/multisave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String multipleUpdateLegalizacion(HttpServletRequest request, @RequestBody String peticion) {
		JSONObject jsonTemp = new JSONObject(peticion);
		Integer reportType = !jsonTemp.isNull("reportType") ? jsonTemp.getInt("reportType") : null;
		String field = !jsonTemp.isNull("field") ? jsonTemp.getString("field") : null;
		String newValue = !jsonTemp.isNull("newValue") ? jsonTemp.getString("newValue") : null;
		String obras = !jsonTemp.isNull("obras") ? jsonTemp.getString("obras") : null;
		String sortFilter = !jsonTemp.isNull("sortFilter") ? jsonTemp.getString("sortFilter") : null;
		String dataFilter = !jsonTemp.isNull("dataFilter") ? jsonTemp.getString("dataFilter") : null;

		String authorization = request.getHeader("Authorization");
		String user = null;
		if (authorization != null && authorization != "") {
			user = jwtTokenUtil.getUsernameFromToken(authorization);
		}

		return legalizacionService.multipleUpdateLegalizacion(field, newValue, obras, sortFilter, dataFilter,
				reportType, user);
	}

	@RequestMapping(value = "/get-option-list-modal", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getOptionListModal(@RequestBody String peticion) {
		JSONObject jsonTemp = new JSONObject(peticion);
		String herramienta = !jsonTemp.isNull("herramienta") ? jsonTemp.getString("herramienta") : null;
		String campo = !jsonTemp.isNull("campo") ? jsonTemp.getString("campo") : null;

		return legalizacionService.getOptionListModal(herramienta, campo);
	}

	@RequestMapping(value = "/project-settings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getProjectSettings() {
		String proyectList = this.generalService.getProyectos();

		return proyectList;
	}

	@RequestMapping(value = "/update-project", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String updateProject(@RequestBody String peticion) {
		JSONObject jsonTemp = new JSONObject(peticion);
		String pryId = !jsonTemp.isNull("PRY_Id") ? jsonTemp.getString("PRY_Id") : null;
		String pryNombre = !jsonTemp.isNull("PRY_Nombre") ? jsonTemp.getString("PRY_Nombre") : null;
		int opt = !jsonTemp.isNull("opt") ? jsonTemp.getInt("opt") : null;
		String proyectList = this.generalService.updateProject(pryId, pryNombre, opt);

		return proyectList;
	}

	@RequestMapping(value = "/load-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String loadData(@RequestBody String peticion) {
		JSONObject jsonTemp = new JSONObject(peticion);
		Integer reportType = !jsonTemp.isNull("reportType") ? jsonTemp.getInt("reportType") : null;
		JSONArray data = !jsonTemp.isNull("data") ? jsonTemp.getJSONArray("data") : new JSONArray();

		return legalizacionService.loadData(data.toString(), reportType);
	}

	@RequestMapping(value = "/audit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAudit(@RequestBody String peticion) {
		JSONObject jsonTemp = new JSONObject(peticion);
		String startTime = !jsonTemp.isNull("startTime") ? jsonTemp.getString("startTime") : null;
		String endTime = !jsonTemp.isNull("endTime") ? jsonTemp.getString("endTime") : null;
		return auditService.getAudit(startTime, endTime);
	}
}
