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

	public String getStoreProcedureInfo(int page, int size, String sortFilter, String dataFilter) {

		String callProcedure = "EXEC [dbo].[SP_Legalizacion_test] @obras = '''149-102'',''152-7'',''152-8'',''96-2'',''96-5'',''96-8'',''139-10'',''139-11'',''96-3'',''108-100'',''108-101'',''2-526'',''42401'',''2-449'',''2-498'',''2-499'',''2-500'',''96-33'',''2-776'',''2-777'',''128-10'',''2-331'',''123-101'',''123-102'',''96-35'',''96-36'',''159-1'',''153-1'',''153-3'',''2-337'',''2-369'',''2-346'',''131-100'',''131-101'',''2-345'',''107-121'',''107-122'',''2-537'',''2-739'',''2-740'',''2-376'',''42036'',''13912'',''2-116'',''2-117'',''2-557'',''2-582'',''2-583'',''2-584'',''2-548'',''2-538'',''2-435'',''87-101'',''87-102'',''87-103'',''2-301'',''108-105'',''108-106'',''152-9'',''133-107'',''2-442'',''80-447'',''139-2'',''139-3'',''12816'',''2-452'',''111-146'',''111-147'',''96-37'',''129-100'',''129-101'',''147-101'',''147-100'',''157-1'',''157-2'',''96-9'',''96-10'',''96-11'',''77-516'',''77-509'',''77-513'',''77-510'',''77-514'',''93-101'',''93-102'',''93-103'',''93-104'',''93-105'',''96-25'',''96-26'',''2-326'',''153-2'',''153-8'',''153-12'',''153-7'',''153-11'',''153-6'',''153-10'',''153-9'',''153-4'',''153-5'',''2-465'',''124-100'',''124-101'',''124-102'',''156-1'',''156-2'',''156-3'',''2-417'',''2-382'',''2-667'',''2-668'',''2-715'',''140-100'',''2-400'',''152-10'',''133-111'',''96-21'',''96-22'',''45323'',''152-1'',''152-2'',''2-406'',''161-100'',''2-695'',''2-696'',''2-693'',''2-694'',''161-101'',''2-366'',''107-109'',''107-129'',''107-130'',''107-133'',''107-145'',''107-136'',''107-134'',''107-135'',''160-138'',''160-139'',''160-140'',''160-141'',''107-110'',''160-142'',''160-143'',''160-144'',''160-1'',''160-145'',''160-146'',''160-147'',''162-100'',''162-101'',''162-102'',''107-111'',''162-104'',''162-105'',''162-106'',''107-116'',''107-117'',''107-123'',''107-126'',''107-127'',''107-128'',''152-11'',''2-448'',''2-338'',''2-541'',''2-565'',''2-813'',''130-4'',''130-5'',''139-8'',''2-564'',''2-431'',''2-462'',''112-113'',''112-114'',''112-1107'',''112-1108'',''112-1109'',''112-1110'',''112-1112'',''112-1113'',''2-380'',''2-383'',''44775'',''2-294'',''28522'',''158-2'',''158-4'',''158-3'',''74-1'',''74-2'',''74-3'',''2-373'',''138-2'',''132-100'',''132-101'',''2-839'',''2-840''' , @Opt = '1'";

		Query queryCount = entityManager.createNativeQuery(callProcedure);
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

		String callProcedureData = "EXEC [dbo].[SP_Legalizacion_test] @obras = '''149-102''' , @PageNumber = '" + page
				+ "',   @RowspPage = '" + size + "'";
		if (sortFilter != null) {
			callProcedureData = "EXEC [dbo].[SP_Legalizacion_test] @obras = '''149-102''', @PageNumber = '" + page
					+ "', @RowspPage = '" + size + "' , @SortFilter = '" + sortFilter + "'";
		}

		if (dataFilter != null) {
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
//		resultadoJSON.put("typeDate", typeDataHeader);
		resultadoJSON.put("data", resultData);
		resultadoJSON.put("count", totalNumber);
		return resultadoJSON.toString();
//		r = r.replace("\n", "").replace("\r", "").replace("\t", "");
//		r = r.strip();
//		r = r.trim();
//		return resultHeaders;
	}

	public List getStoreProcedureHeaderOptionList(String dataFilter, String dataGroup) {
		String callProcedureData = "EXEC [dbo].[SP_Legalizacion_test] @obras = '''149-102''' , @Opt = '3',   @DataGroup = '"
				+ dataGroup + "'";
		
		if (dataFilter != null) {
			callProcedureData = callProcedureData + " , @DataFilter = '" + dataFilter + "' ";
			System.out.println(callProcedureData);
		}

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		List resultData = nativeQueryData.getResultList();

		return resultData;
	}
}
