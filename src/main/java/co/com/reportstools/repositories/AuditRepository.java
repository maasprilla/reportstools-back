package co.com.reportstools.repositories;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

@Repository
public class AuditRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public String getAudit(String startTime, String endTime) {

		String callProcedureDataSelected = "SELECT * FROM [MacrosWebApp].[dbo].[tbl_auditoria] WHERE (FORMAT (fecha_modificacion, 'yyyy-MM-dd') BETWEEN '"+startTime+"' AND '"+endTime+"' )  ORDER BY fecha_modificacion DESC ";

		Query queryDataSelected = entityManager.createNativeQuery(callProcedureDataSelected);
		NativeQueryImpl nativeQueryDataSelected = (NativeQueryImpl) queryDataSelected;
		nativeQueryDataSelected.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Map<String, Object>> resultDataSelected = nativeQueryDataSelected.getResultList();

		JSONObject resultadoJSON = new JSONObject();
		resultadoJSON.put("data", resultDataSelected);
		resultadoJSON.put("code", 0);

		return resultadoJSON.toString();
	}

}
