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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Repository
public class LegalizacionRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public String getStoreProcedureInfo(String obras, int page, int size, String sortFilter, String dataFilter) {

		String callProcedure = "EXEC [dbo].[SP_Legalizacion_test] @obras = '" + obras + "' , @Opt = '1'";
		if (dataFilter != null) {

			dataFilter = dataFilter.replace("Invalid Date", "null");
			dataFilter = dataFilter.replace("Vacio", "null");
			dataFilter = dataFilter.replace("''null''", "NULL");
			callProcedure = callProcedure + " , @DataFilter = '" + dataFilter + "' ";
			System.out.println(callProcedure);
		}

		Query queryCount = entityManager.createNativeQuery(callProcedure);
		NativeQueryImpl nativeQuery = (NativeQueryImpl) queryCount;
		String result = String.valueOf(nativeQuery.getSingleResult());
		Long totalNumber = Long.parseLong(result);
		System.out.println(result);

		Query queryHeaders = entityManager
				.createNativeQuery("EXEC [dbo].[SP_Legalizacion_test] @obras = '" + obras + "' , @Opt = '2'");
		NativeQueryImpl nativeQueryHeaders = (NativeQueryImpl) queryHeaders;
		String resultHeaders = String.valueOf(nativeQueryHeaders.getSingleResult());
		String[] resultHeadersList = resultHeaders.split(",");

		String callProcedureData = "EXEC [dbo].[SP_Legalizacion_test] @obras = '" + obras + "' , @PageNumber = '" + page
				+ "',   @RowspPage = '" + size + "'";
		if (sortFilter != null) {
			callProcedureData = "EXEC [dbo].[SP_Legalizacion_test] @obras = '" + obras + "', @PageNumber = '" + page
					+ "', @RowspPage = '" + size + "' , @SortFilter = '" + sortFilter + "'";
		}

		if (dataFilter != null) {

			dataFilter = dataFilter.replace("Invalid Date", "null");
			dataFilter = dataFilter.replace("Vacio", "null");
			dataFilter = dataFilter.replace("''null''", "NULL");
			callProcedureData = callProcedureData + " , @DataFilter = '" + dataFilter + "' ";
			System.out.println(callProcedureData);
		}

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		nativeQueryData.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Map<String, Object>> resultData = nativeQueryData.getResultList();

//
//		String typeDataHeader[] = null;
//		String nameHeader[] = null;

//		int count1 = 0;
//		for (Map<String, Object> map : result) {
//			if (count1 == 0) {
//				typeDataHeader = new String[map.size()];
//				nameHeader = new String[map.size()];
////				for (int i = 0; i < map.size(); i++) {
////					JSONObject jsonTemp = new JSONObject();
////					typeDataHeader[i] = new JSONObject();
////				}
//			}
//
//			int count2 = 0;
//			for (Map.Entry<String, Object> entry : map.entrySet()) {
//				nameHeader[count2] = entry.getKey();
//				if (entry.getValue() != null) {
//					if (typeDataHeader != null && typeDataHeader[count2] == null) {
//						typeDataHeader[count2] = entry.getValue().getClass().getName();
//					}
//				}
//				count2++;
//			}
//			count1++;
//		}
//
//		String jsonBook = "";
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			jsonBook = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
//		} catch (JsonProcessingException e) {
//			jsonBook = "[]";
//		}
//
		JSONObject resultadoJSON = new JSONObject();
		resultadoJSON.put("headers", resultHeadersList);
		resultadoJSON.put("data", resultData);
		resultadoJSON.put("count", totalNumber);
		return resultadoJSON.toString();

	}

	public List getStoreProcedureHeaderOptionList(String obras, String dataFilter, String dataGroup) {
		String callProcedureData = "EXEC [dbo].[SP_Legalizacion_test] @obras = '" + obras
				+ "' , @Opt = '3',   @DataGroup = '" + dataGroup + "'";

		if (dataFilter != null) {
			callProcedureData = callProcedureData + " , @DataFilter = '" + dataFilter + "' ";
			System.out.println(callProcedureData);
		}

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		List resultData = nativeQueryData.getResultList();

		return resultData;
	}

	public String updateLegalizacion(String data) {
		String callProcedureData = "EXEC [dbo].[SP__Update_Legalizacion] @data = '" + data + "'";

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		String resultData = (String) nativeQueryData.getSingleResult();

		return resultData;
	}

	public String multipleUpdateLegalizacion(String field, String newValue, String obras, String sortFilter,
			String dataFilter) {
		
		newValue = newValue.replace("Vacio", "NULL");


		String callProcedureData = "EXEC [dbo].[SP_Legalizacion_test] @obras = '" + obras + "'";
		if (sortFilter != null) {
			callProcedureData = callProcedureData + " , @SortFilter = '" + sortFilter + "' ";
		}

		if (dataFilter != null) {
			callProcedureData = callProcedureData + " , @DataFilter = '" + dataFilter + "' ";
			System.out.println(callProcedureData);
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

		System.out.println(peticion);

		String callProcedureUpdate = "EXEC [dbo].[SP__Update_Legalizacion] @data = '" + peticion + "'";

		Query queryDataUpdate = entityManager.createNativeQuery(callProcedureUpdate);
		NativeQueryImpl nativeQueryUpdate = (NativeQueryImpl) queryDataUpdate;
		String resultDataUpdate = (String) nativeQueryUpdate.getSingleResult();

		JSONObject resp = new JSONObject();
		resp.put("code", 0);
		resp.put("message", resultDataUpdate);

		return resp.toString();
	}
}
