package co.com.reportstools.repositories;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Repository
public class LegalizacionRepository {

	@PersistenceContext
	private EntityManager entityManager;

	final int LEGALIZACION = 1;
	final int PROMESA = 2;
	final int SUBSIDIO = 3;
	final int SEGUIMIENTO_SUBSIDIO = 4;
	final int ENTREGA = 5;
	final int DESEMBOLSO = 6;
	final int RECAUDO = 7;
	final int TRAZABILIDAD = 8;

	final String STORE_PROCEDURE_LEGALIZACION = "sp_legalizacion";
	final String STORE_PROCEDURE_PROMESA = "sp_promesas";
	final String STORE_PROCEDURE_SUBSIDIO = "sp_subsidio";
	final String STORE_PROCEDURE_SEGUIMIENTO_SUBSIDIO = "sp_segsubsidio";
	final String STORE_PROCEDURE_ENTREGA = "sp_entregas";
	final String STORE_PROCEDURE_DESEMBOLSO = "sp_desembolso";
	final String STORE_PROCEDURE_RECAUDO = "";
	final String STORE_PROCEDURE_TRAZABILIDAD = "sp_trazabilidad";

	public String getStoreProcedureInfo(String obras, int page, int size, String sortFilter, String dataFilter,
			int reportType) {

		String storeProcedureName = this.getStoreProcedureName(reportType);

		obras = this.getProyectos();

		String callProcedure = "EXEC [dbo].[" + storeProcedureName + "] @obras = '" + obras + "' , @Opt = '1'";
		if (dataFilter != null) {

			dataFilter = dataFilter.replace("Invalid Date", "null");
			dataFilter = dataFilter.replace("Vacio", "null");
			dataFilter = dataFilter.replace("''null''", "NULL");
			callProcedure = callProcedure + " , @DataFilter = '" + dataFilter + "' ";
//			System.out.println(callProcedure);
		}

		Query queryCount = entityManager.createNativeQuery(callProcedure);
		NativeQueryImpl nativeQuery = (NativeQueryImpl) queryCount;
		String result = String.valueOf(nativeQuery.getSingleResult());
		Long totalNumber = Long.parseLong(result);
//		System.out.println(result);

		Query queryHeaders = entityManager
				.createNativeQuery("EXEC [dbo].[" + storeProcedureName + "] @obras = '" + obras + "' , @Opt = '2'");
		NativeQueryImpl nativeQueryHeaders = (NativeQueryImpl) queryHeaders;
		String resultHeaders = String.valueOf(nativeQueryHeaders.getSingleResult());
		String[] resultHeadersList = resultHeaders.split(",");

		String callProcedureData = "EXEC [dbo].[" + storeProcedureName + "] @obras = '" + obras + "' , @PageNumber = '"
				+ page + "',   @RowspPage = '" + size + "'";
		if (sortFilter != null) {
			callProcedureData = "EXEC [dbo].[" + storeProcedureName + "] @obras = '" + obras + "', @PageNumber = '"
					+ page + "', @RowspPage = '" + size + "' , @SortFilter = '" + sortFilter + "'";
		}

		if (dataFilter != null) {

			dataFilter = dataFilter.replace("Invalid Date", "null");
			dataFilter = dataFilter.replace("Vacio", "null");
			dataFilter = dataFilter.replace("''null''", "NULL");
			callProcedureData = callProcedureData + " , @DataFilter = '" + dataFilter + "' ";
//			System.out.println(callProcedureData);
		}

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		nativeQueryData.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Map<String, Object>> resultData = nativeQueryData.getResultList();

		GsonBuilder builder = new GsonBuilder();
		builder.serializeNulls();
		Gson gson = builder.setPrettyPrinting().create();
		String jsonEmp = gson.toJson(resultData);

		JSONObject resultadoJSON = new JSONObject();
		resultadoJSON.put("headers", resultHeadersList);
		resultadoJSON.put("data", jsonEmp);
		resultadoJSON.put("count", totalNumber);
		return resultadoJSON.toString();

	}

	public List getStoreProcedureHeaderOptionList(String obras, String dataFilter, String dataGroup, int reportType) {

		String storeProcedureName = this.getStoreProcedureName(reportType);

		obras = this.getProyectos();

		String callProcedureData = "EXEC [dbo].[" + storeProcedureName + "] @obras = '" + obras
				+ "' , @Opt = '3',   @DataGroup = '" + dataGroup + "'";

		if (dataFilter != null) {
			callProcedureData = callProcedureData + " , @DataFilter = '" + dataFilter + "' ";
//			System.out.println(callProcedureData);
		}

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		List resultData = nativeQueryData.getResultList();

		return resultData;
	}

	public List getStoreProcedureHeaderOptionFilterList(String obras, String dataFilter, String dataGroup,
			String filterLike, int reportType) {

		String storeProcedureName = this.getStoreProcedureName(reportType);

		obras = this.getProyectos();

		String callProcedureData = "EXEC [dbo].[" + storeProcedureName + "] @obras = '" + obras
				+ "' , @Opt = '4',   @DataGroup = '" + dataGroup + "'";

		if (dataFilter != null) {
			callProcedureData = callProcedureData + " , @DataFilter = '" + dataFilter + "' ";
		}

		if (filterLike != null) {
			callProcedureData = callProcedureData + " , @FilterLike = '" + filterLike + "' ";
		}

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		List resultData = nativeQueryData.getResultList();

		return resultData;
	}

	public String updateLegalizacion(String data, int reportType) {
		String callProcedureData = "EXEC [dbo].[sp_update_legalizacion] @data = '" + data + "' , @reportType = '"+reportType+"'  ";

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		String resultData = (String) nativeQueryData.getSingleResult();

		return resultData;
	}

	public String multipleUpdateLegalizacion(String field, String newValue, String obras, String sortFilter,
			String dataFilter, int reportType) {

		String storeProcedureName = this.getStoreProcedureName(reportType);

		newValue = newValue.replace("Vacio", "NULL");

		String callProcedureData = "EXEC [dbo].[" + storeProcedureName + "] @obras = '" + obras + "'";
		if (sortFilter != null) {
			callProcedureData = callProcedureData + " , @SortFilter = '" + sortFilter + "' ";
		}

		if (dataFilter != null) {
			callProcedureData = callProcedureData + " , @DataFilter = '" + dataFilter + "' ";
//			System.out.println(callProcedureData);
		}

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		nativeQueryData.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Map<String, Object>> resultData = nativeQueryData.getResultList();

		String peticion = "";
		for (Map<String, Object> map : resultData) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if (entry.getKey().equals(field)) {
					entry.setValue(newValue);
				}
			}

			String entryInsert = "INSERT INTO #tblTEMP  \n"
					+ "    ([UNI_ID],[FechaSeguimientoCoordinador],[EstadoCoordinador],[ObservacionCoordinador],[FechaSeguimientoAnalista],[EstadoAnalista],\n"
					+ "      [ObservacionAnalista],[FechaAsignacion],[AsignacionAnalista],[AnalistaVarado],[FechaAsignacionVarado],[FechaDesvarado] ) \n"
					+ "      VALUES (''" + map.get("UNI_ID") + "'',\n" + "      ''"
					+ map.get("FechaSeguimientoCoordinador") + "'',\n" + "      ''" + map.get("EstadoCoordinador")
					+ "'',\n" + "      ''" + map.get("ObservacionCoordinador") + "'',\n" + "      ''"
					+ map.get("FechaSeguimientoAnalista") + "'',\n" + "      ''" + map.get("EstadoAnalista") + "'',\n"
					+ "      ''" + map.get("ObservacionAnalista") + "'',\n" + "      ''" + map.get("FechaAsignacion")
					+ "'',\n" + "      ''" + map.get("AsignacionAnalista") + "'',\n" + "      ''"
					+ map.get("AnalistaVarado") + "'',\n" + "      ''" + map.get("FechaAsignacionVarado") + "'',\n"
					+ "      ''" + map.get("FechaDesvarado") + "'')";

			entryInsert = entryInsert.replace("Invalid Date", "null");
			entryInsert = entryInsert.replace("''null''", "NULL");
			entryInsert = entryInsert + " ";
			peticion = peticion + entryInsert;
		}

		peticion = peticion.replace("Invalid Date", "null");
		peticion = peticion.replace("Vacio", "null");
		peticion = peticion.replace("NULL", "null");
		peticion = peticion.replace("''null''", "NULL");

//		System.out.println(peticion);

		String callProcedureUpdate = "EXEC [dbo].[sp_update_legalizacion] @data = '" + peticion + "'";

		Query queryDataUpdate = entityManager.createNativeQuery(callProcedureUpdate);
		NativeQueryImpl nativeQueryUpdate = (NativeQueryImpl) queryDataUpdate;
		String resultDataUpdate = (String) nativeQueryUpdate.getSingleResult();

		JSONObject resp = new JSONObject();
		resp.put("code", 0);
		resp.put("message", resultDataUpdate);

		return resp.toString();
	}

	public String getProyectos() {
		String callProcedureData = "SELECT PRY_ID FROM [MacrosWebApp].[dbo].[tbl_proyectos] GROUP BY PRY_ID";

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		List<String> resultData = (List<String>) nativeQueryData.getResultList();

		String obras = "";
		for (int i = 0; i < resultData.size(); i++) {
			if (i < (resultData.size() - 1)) {
				obras += "''" + resultData.get(i) + "'',";
			} else {
				obras += "''" + resultData.get(i) + "''";
			}
		}

		return obras;
	}

	private String getStoreProcedureName(int id) {
		if (this.LEGALIZACION == id) {
			return STORE_PROCEDURE_LEGALIZACION;
		} else if (this.PROMESA == id) {
			return STORE_PROCEDURE_PROMESA;
		} else if (this.SUBSIDIO == id) {
			return STORE_PROCEDURE_SUBSIDIO;
		} else if (this.SEGUIMIENTO_SUBSIDIO == id) {
			return STORE_PROCEDURE_SEGUIMIENTO_SUBSIDIO;
		} else if (this.ENTREGA == id) {
			return STORE_PROCEDURE_ENTREGA;
		} else if (this.DESEMBOLSO == id) {
			return STORE_PROCEDURE_DESEMBOLSO;
		} else if (this.RECAUDO == id) {
			return STORE_PROCEDURE_RECAUDO;
		} else if (this.TRAZABILIDAD == id) {
			return STORE_PROCEDURE_TRAZABILIDAD;
		}

		return null;
	}
}
