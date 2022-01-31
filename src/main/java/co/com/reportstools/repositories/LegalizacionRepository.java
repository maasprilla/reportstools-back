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

	public String getStoreProcedureInfo(int page, int size) {
//		Query queryCount = entityManager
//				.createNativeQuery("EXEC [dbo].[SP_Legalizacion_test] @obras = '''149-102''' , @PageNumber = '" + page
//						+ "',   @RowspPage = '" + size + "'");
		Query queryCount = entityManager
				.createNativeQuery("EXEC [dbo].[SP_Legalizacion_test] @obras = '''149-102''' , @Opt = '1'");
		NativeQueryImpl nativeQuery = (NativeQueryImpl) queryCount;
		// nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
//		List<Map<String, Object>> result = nativeQuery.getResultList();
		String result = String.valueOf(nativeQuery.getSingleResult());
		Long totalNumber = Long.parseLong(result);
		System.out.println(result);

		Query queryHeaders = entityManager
				.createNativeQuery("EXEC [dbo].[SP_Legalizacion_test] @obras = '''149-102''' , @Opt = '2'");
		NativeQueryImpl nativeQueryHeaders = (NativeQueryImpl) queryHeaders;
		String resultHeaders = String.valueOf(nativeQueryHeaders.getSingleResult());
		String[] resultHeadersList = resultHeaders.split(",");
		
		
		Query queryData = entityManager
		.createNativeQuery("EXEC [dbo].[SP_Legalizacion_test] @obras = '''149-102''' , @PageNumber = '" + page
				+ "',   @RowspPage = '" + size + "'");
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
//		resultadoJSON.put("typeDate", typeDataHeader);
		resultadoJSON.put("data", resultData);
		resultadoJSON.put("count", totalNumber);
		return resultadoJSON.toString();
//		r = r.replace("\n", "").replace("\r", "").replace("\t", "");
//		r = r.strip();
//		r = r.trim();
//		return resultHeaders;
	}
}
