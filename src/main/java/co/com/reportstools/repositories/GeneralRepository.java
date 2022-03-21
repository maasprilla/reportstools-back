package co.com.reportstools.repositories;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

@Repository
public class GeneralRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public String getProyectos() {

		String callProcedureData = "EXEC [dbo].[sp_proyectos] ";

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		nativeQueryData.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Map<String, Object>> resultData = nativeQueryData.getResultList();

		String callProcedureDataSelected = "SELECT * FROM [MacrosWebApp].[dbo].[tbl_proyectos] ORDER BY PRY_Nombre ";

		Query queryDataSelected = entityManager.createNativeQuery(callProcedureDataSelected);
		NativeQueryImpl nativeQueryDataSelected = (NativeQueryImpl) queryDataSelected;
		nativeQueryDataSelected.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Map<String, Object>> resultDataSelected = nativeQueryDataSelected.getResultList();

		JSONArray projectList = new JSONArray();
		for (Map<String, Object> map : resultData) {
			boolean result = this.getStatusProject(map.get("PRY_ID").toString(), resultDataSelected);
			JSONObject tempObject = new JSONObject();
			tempObject.put("PRY_ID", map.get("PRY_ID").toString());
			tempObject.put("PRY_Nombre", map.get("PRY_Nombre").toString());
			if (result) {
				tempObject.put("STATUS", true);
			} else {
				tempObject.put("STATUS", false);
			}
			projectList.put(tempObject);
		}

//		GsonBuilder builder = new GsonBuilder();
//		builder.serializeNulls();
//		Gson gson = builder.setPrettyPrinting().create();
//		String jsonEmp = gson.toJson(resultDataSelected);

		JSONObject resultadoJSON = new JSONObject();
		resultadoJSON.put("data", projectList);
		return resultadoJSON.toString();
	}

	@Transactional
	public String updateProject(String pryId, String pryNombre, int opt) {

		String callProcedureDataSelected = "INSERT INTO [MacrosWebApp].[dbo].[tbl_proyectos] (PRY_ID,PRY_Nombre) VALUES('"
				+ pryId + "','" + pryNombre + "') ";
		System.out.println(callProcedureDataSelected);
		if (opt == 2) {
			callProcedureDataSelected = "DELETE FROM [MacrosWebApp].[dbo].[tbl_proyectos] WHERE PRY_ID = '"+pryId+"' AND PRY_Nombre = '"+pryNombre+"'";
		}
		Query queryDataSelected = entityManager.createNativeQuery(callProcedureDataSelected);
		NativeQueryImpl nativeQueryDataSelected = (NativeQueryImpl) queryDataSelected;
		int result = nativeQueryDataSelected.executeUpdate();
		JSONObject resultadoJSON = new JSONObject();

		resultadoJSON.put("code", 0);
		return resultadoJSON.toString();
	}

	private boolean getStatusProject(String project, List<Map<String, Object>> list) {
		for (Map<String, Object> map : list) {
			if (map.get("PRY_ID").toString().equals(project)) {
				return true;
			}
		}
		return false;
	}

}
