package co.com.reportstools.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import co.com.reportstools.models.Audit;

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
	final int ORDENES = 9;
	final int RENOVACION = 10;
	final int ESCRITURACION = 11;

	final String STORE_PROCEDURE_LEGALIZACION = "sp_legalizacion";
	final String STORE_PROCEDURE_PROMESA = "sp_promesas";
	final String STORE_PROCEDURE_SUBSIDIO = "sp_subsidio";
	final String STORE_PROCEDURE_SEGUIMIENTO_SUBSIDIO = "sp_segsubsidio";
	final String STORE_PROCEDURE_ENTREGA = "sp_entregas";
	final String STORE_PROCEDURE_DESEMBOLSO = "sp_desembolso";
	final String STORE_PROCEDURE_RECAUDO = "sp_recaudo";
	final String STORE_PROCEDURE_TRAZABILIDAD = "sp_trazabilidad";
	final String STORE_PROCEDURE_ORDENES = "sp_ordenes";
	final String STORE_PROCEDURE_RENOVACION = "sp_renovacion";
	final String STORE_PROCEDURE_ESCRITURACION = "sp_escrituracion";

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
		if (resultHeaders.contains("(")) {
			resultHeaders = this.processHeaderWithCalculations(resultHeaders);
		} else {
			resultHeaders = resultHeaders.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
		}

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

	@Transactional
	public String updateLegalizacion(String data, int reportType, List auditList, String user) {
		String callProcedureData = "EXEC [dbo].[sp_update_legalizacion] @data = '" + data + "' , @reportType = '"
				+ reportType + "'  ";

		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		String resultData = (String) nativeQueryData.getSingleResult();

		String herramienta = "";
		if (reportType == this.LEGALIZACION) {
			herramienta = "Legalizacion";
		} else if (reportType == this.PROMESA) {
			herramienta = "Promesas";
		} else if (reportType == this.SEGUIMIENTO_SUBSIDIO) {
			herramienta = "SegSubsidio";
		} else if (reportType == this.ENTREGA) {
			herramienta = "Entregas";
		} else if (reportType == this.DESEMBOLSO) {
			herramienta = "Desembolso";
		} else if (reportType == this.TRAZABILIDAD) {
			herramienta = "Trazabilidad";
		} else if (reportType == this.SUBSIDIO) {
			herramienta = "Subsidio";
		} else if (reportType == this.ORDENES) {
			herramienta = "Ordenes";
		} else if (reportType == this.RENOVACION) {
			herramienta = "Renovacion";
		} else if (reportType == this.ESCRITURACION) {
			herramienta = "Escrituracion";
		} else if (reportType == this.RECAUDO) {
			herramienta = "Recaudo";
		}

		for (int i = 0; i < auditList.size(); i++) {
			Map<String, Object> map = (Map<String, Object>) auditList.get(i);
			String name = map.get("name") != null ? map.get("name").toString() : null;
			String before = map.get("before") != null ? map.get("before").toString() : null;
			String after = map.get("after") != null ? map.get("after").toString() : null;
			String UNI_ID = map.get("UNI_ID") != null ? map.get("UNI_ID").toString() : null;

			System.out.println(map);
			if (before == null || !before.equals(after)) {
				this.auditData(UNI_ID, herramienta, name, before, after, user);
			}
		}

		return resultData;
	}

	@Transactional
	public String multipleUpdateLegalizacion(String field, String newValue, String obras, String sortFilter,
			String dataFilter, int reportType, String user) {

		try {
			String storeProcedureName = this.getStoreProcedureName(reportType);

			if (newValue != null) {
				newValue = newValue.replace("Vacio", "NULL");
			}

			String callProcedureData = "EXEC [dbo].[" + storeProcedureName + "] @obras = '" + obras + "'";
			if (sortFilter != null) {
				callProcedureData = callProcedureData + " , @SortFilter = '" + sortFilter + "' ";
			}

			if (dataFilter != null) {
				callProcedureData = callProcedureData + " , @DataFilter = '" + dataFilter + "' ";
//				System.out.println(callProcedureData);
			}

			Query queryData = entityManager.createNativeQuery(callProcedureData);
			NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
			nativeQueryData.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			List<Map<String, Object>> resultData = nativeQueryData.getResultList();

			List<Audit> auditList = new ArrayList<Audit>();
			String peticion = "";
			for (Map<String, Object> map : resultData) {
				Audit audit = new Audit();
				audit.setUsuario(user);
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if (entry.getKey().equals(field)) {
						audit.setValorAnterior(String.valueOf(entry.getValue()));
						entry.setValue(newValue);
						audit.setValorActual(newValue);
					}
					if (entry.getKey().equals("UNI_ID")) {
						audit.setUNI_ID(String.valueOf(entry.getValue()));
					}
					audit.setCampo(field);
				}

				String entryInsert = "";
				if (reportType == this.LEGALIZACION) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[FechaSeguimientoCoordinador],[EstadoCoordinador],[ObservacionCoordinador],[FechaSeguimientoAnalista],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista],[FechaAsignacion],[AsignacionAnalista],[AnalistaVarado],[FechaAsignacionVarado],[FechaDesvarado] ) \n"
							+ "      VALUES (''" + map.get("UNI_ID") + "'',\n" + "      ''"
							+ map.get("FechaSeguimientoCoordinador") + "'',\n" + "      ''"
							+ map.get("EstadoCoordinador") + "'',\n" + "      ''" + map.get("ObservacionCoordinador")
							+ "'',\n" + "      ''" + map.get("FechaSeguimientoAnalista") + "'',\n" + "      ''"
							+ map.get("EstadoAnalista") + "'',\n" + "      ''" + map.get("ObservacionAnalista")
							+ "'',\n" + "      ''" + map.get("FechaAsignacion") + "'',\n" + "      ''"
							+ map.get("AsignacionAnalista") + "'',\n" + "      ''" + map.get("AnalistaVarado") + "'',\n"
							+ "      ''" + map.get("FechaAsignacionVarado") + "'',\n" + "      ''"
							+ map.get("FechaDesvarado") + "'')";
					audit.setHerramienta("Legalizacion");
				} else if (reportType == this.PROMESA) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista] ) \n" + "      VALUES (''" + map.get("UNI_ID") + "'',\n"
							+ "      ''" + map.get("EstadoCoordinador") + "'',\n" + "      ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "      ''" + map.get("EstadoAnalista")
							+ "'',\n" + "      ''" + map.get("ObservacionAnalista") + "'')";
					audit.setHerramienta("Promesas");
				} else if (reportType == this.SEGUIMIENTO_SUBSIDIO) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista] ) \n" + "      VALUES (''" + map.get("UNI_ID") + "'',\n"
							+ "      ''" + map.get("EstadoCoordinador") + "'',\n" + "      ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "      ''" + map.get("EstadoAnalista")
							+ "'',\n" + "      ''" + map.get("ObservacionAnalista") + "'')";
					audit.setHerramienta("SegSubsidio");
				} else if (reportType == this.ENTREGA) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[FechadeEntrega],[HoradeEntrega],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista], [Arquitecta],[CreacionCaso],[Apoderado]) \n"
							+ "      VALUES (''" + map.get("UNI_ID") + "'',\n" + "      ''" + map.get("FechadeEntrega")
							+ "'',\n" + "      ''" + map.get("HoradeEntrega") + "'',\n" + "      ''"
							+ map.get("EstadoCoordinador") + "'',\n" + "      ''" + map.get("ObservacionCoordinador")
							+ "'',\n" + "      ''" + map.get("EstadoAnalista") + "'',\n" + "      ''"
							+ map.get("ObservacionAnalista") + "'', " + "      ''" + map.get("Arquitecta") + "'', "
							+ "      ''" + map.get("CreacionCaso") + "'', " + "      ''" + map.get("Apoderado")
							+ "'' )";
					audit.setHerramienta("Entregas");
				} else if (reportType == this.DESEMBOLSO) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[Comentarios],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista] ) \n" + "      VALUES (''" + map.get("UNI_ID") + "'',\n"
							+ "      ''" + map.get("EstadoCoordinador") + "'',\n" + "      ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "      ''" + map.get("Comentarios")
							+ "'',\n" + "      ''" + map.get("EstadoAnalista") + "'',\n" + "      ''"
							+ map.get("ObservacionAnalista") + "'')";
					audit.setHerramienta("Desembolso");
				} else if (reportType == this.TRAZABILIDAD) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista],[TipificacionCorreccionesCtl],[NBR],[AnalistaTrazabilidad],[DetalleObservacion] ) \n"
							+ "      VALUES (''" + map.get("UNI_ID") + "'',\n" + "      ''"
							+ map.get("EstadoCoordinador") + "'',\n" + "      ''" + map.get("ObservacionCoordinador")
							+ "'',\n" + "      ''" + map.get("EstadoAnalista") + "'',\n" + "      ''"
							+ map.get("ObservacionAnalista") + "'',\n" + "      ''"
							+ map.get("TipificacionCorreccionesCtl") + "'',\n" + "      ''" + map.get("NBR") + "'',\n"
							+ "      ''" + map.get("AnalistaTrazabilidad") + "'',\n" + "      ''"
							+ map.get("DetalleObservacion") + "'')";
					audit.setHerramienta("Trazabilidad");
				} else if (reportType == this.SUBSIDIO) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[Comentarios],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista] ) \n" + "      VALUES (''" + map.get("UNI_ID") + "'',\n"
							+ "      ''" + map.get("EstadoCoordinador") + "'',\n" + "      ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "      ''" + map.get("Comentarios")
							+ "'',\n" + "      ''" + map.get("EstadoAnalista") + "'',\n" + "      ''"
							+ map.get("ObservacionAnalista") + "'')";
					audit.setHerramienta("Subsidio");
				} else if (reportType == this.ORDENES) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "      ([UNI_ID],[Frpl],[QuienFirmaEsfp],[EstadoEsfpFrpl],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],[ObservacionAnalista],[EstadoAbogado],\n"
							+ "        [ObservacionAbogado],[CualFueElCambio],[NoPredialNacional],[NumeroId],[IdMunicipio],[RadicaciónOrdenPagoPySPredial],[FechaEstimadaDePyS],\n"
							+ "        [TipoPyS],[MunicipioPyS],[EstadoPyS],[EstadoOrdenes] ) \n" + "        VALUES (''"
							+ map.get("UNI_ID") + "'',\n" + "        ''" + map.get("Frpl") + "'',\n" + "        ''"
							+ map.get("QuienFirmaEsfp") + "'',\n" + "        ''" + map.get("EstadoEsfpFrpl") + "'',\n"
							+ "        ''" + map.get("EstadoCoordinador") + "'',\n" + "        ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "        ''" + map.get("EstadoAnalista")
							+ "'',\n" + "        ''" + map.get("ObservacionAnalista") + "'',\n" + "        ''"
							+ map.get("EstadoAbogado") + "'',\n" + "        ''" + map.get("ObservacionAbogado")
							+ "'',\n" + "        ''" + map.get("CualFueElCambio") + "'',\n" + "        ''"
							+ map.get("NoPredialNacional") + "'',\n" + "        ''" + map.get("NumeroId") + "'',\n"
							+ "        ''" + map.get("IdMunicipio") + "'',\n" + "        ''"
							+ map.get("RadicaciónOrdenPagoPySPredial") + "'',\n" + "        ''"
							+ map.get("FechaEstimadaDePyS") + "'',\n" + "        ''" + map.get("TipoPyS") + "'',\n"
							+ "        ''" + map.get("MunicipioPyS") + "'',\n" + "        ''" + map.get("EstadoPyS")
							+ "'',\n" + "        ''" + map.get("EstadoOrdenes") + "'')";
					audit.setHerramienta("Ordenes");
				} else if (reportType == this.RENOVACION) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "      ([UNI_ID],[FechaSeguimientoCoordinador],[EstadoCoordinador],[ObservacionCoordinador],[CampanasEspeciales],[CausalRenovacion],[FechaSeguimientoAnalista],[EstadoAnalista],[ObservacionAnalista],[EstadoPoderAnalista],[Asignacion],[Broker],[FechaAsignacion] ) \n"
							+ "        VALUES (''" + map.get("UNI_ID") + "'',\n" + "        ''"
							+ map.get("FechaSeguimientoCoordinador") + "'',\n" + "        ''"
							+ map.get("EstadoCoordinador") + "'',\n" + "        ''" + map.get("ObservacionCoordinador")
							+ "'',\n" + "        ''" + map.get("CampanasEspeciales") + "'',\n" + "        ''"
							+ map.get("CausalRenovacion") + "'',\n" + "        ''" + map.get("FechaSeguimientoAnalista")
							+ "'',\n" + "        ''" + map.get("EstadoAnalista") + "'',\n" + "        ''"
							+ map.get("ObservacionAnalista") + "'',\n" + "        ''" + map.get("EstadoPoderAnalista")
							+ "'',\n" + "        ''" + map.get("Asignacion") + "'',\n" + "        ''"
							+ map.get("Broker") + "'',\n" + "        ''" + map.get("FechaAsignacion") + "'')";
					audit.setHerramienta("Renovacion");
				} else if (reportType == this.ESCRITURACION) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista] ) \n" + "      VALUES (''" + map.get("UNI_ID") + "'',\n"
							+ "      ''" + map.get("EstadoCoordinador") + "'',\n" + "      ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "      ''" + map.get("EstadoAnalista")
							+ "'',\n" + "      ''" + map.get("ObservacionAnalista") + "'')";
					audit.setHerramienta("Escrituracion");
				} else if (reportType == this.RECAUDO) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "      ( [UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],[ObservacionAnalista],[FechaAcuerdoProximoPago],[MontoPactado],[LlamadaNo1],[LlamadaNo2],[LlamadaNo3],[LlamadaNo4],[LlamadaNo5],[EstadoEscriturado] ) \n"
							+ "        VALUES (''" + map.get("UNI_ID") + "'',\n" + "        ''"
							+ map.get("EstadoCoordinador") + "'',\n" + "        ''" + map.get("ObservacionCoordinador")
							+ "'',\n" + "        ''" + map.get("EstadoAnalista") + "'',\n" + "        ''"
							+ map.get("ObservacionAnalista") + "'',\n" + "        ''"
							+ map.get("FechaAcuerdoProximoPago") + "'',\n" + "        ''" + map.get("MontoPactado")
							+ "'',\n" + "        ''" + map.get("LlamadaNo1") + "'',\n" + "        ''"
							+ map.get("LlamadaNo2") + "'',\n" + "        ''" + map.get("LlamadaNo3") + "'',\n"
							+ "        ''" + map.get("LlamadaNo4") + "'',\n" + "        ''" + map.get("LlamadaNo5")
							+ "'',\n" + "        ''" + map.get("EstadoEscriturado") + "'')";
					audit.setHerramienta("Recaudo");
				}

				entryInsert = entryInsert.replace("Invalid Date", "null");
				entryInsert = entryInsert.replace("''null''", "NULL");
				entryInsert = entryInsert + " ";
				peticion = peticion + entryInsert;

				auditList.add(audit);
			}

			peticion = peticion.replace("Invalid Date", "null");
			peticion = peticion.replace("Vacio", "null");
			peticion = peticion.replace("NULL", "null");
			peticion = peticion.replace("''null''", "NULL");

//			System.out.println(peticion);

			String callProcedureUpdate = "EXEC [dbo].[sp_update_legalizacion] @data = '" + peticion + "'"
					+ " , @reportType = '" + reportType + "'  ";

			Query queryDataUpdate = entityManager.createNativeQuery(callProcedureUpdate);
			NativeQueryImpl nativeQueryUpdate = (NativeQueryImpl) queryDataUpdate;
			String resultDataUpdate = (String) nativeQueryUpdate.getSingleResult();

			if (user != null && user != "") {
				for (Audit auditTemp : auditList) {
					if (!auditTemp.getValorAnterior().equals(auditTemp.getValorActual())) {
						this.auditData(auditTemp.getUNI_ID(), auditTemp.getHerramienta(), auditTemp.getCampo(),
								auditTemp.getValorAnterior(), auditTemp.getValorActual(), user);
					}
				}
			}

			JSONObject resp = new JSONObject();
			resp.put("code", 0);
			resp.put("message", resultDataUpdate);

			return resp.toString();
		} catch (Exception e) {
			JSONObject resp = new JSONObject();
			resp.put("code", 1);
			resp.put("message", e.getMessage());
			return resp.toString();
		}
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

	public List<String> getOptionListModal(String herramienta, String campo) {
		String callProcedureData = "SELECT Opcion FROM [MacrosWebApp].[dbo].[tbl_options] WHERE Herramienta = '"
				+ herramienta + "' AND Campo = '" + campo + "' GROUP BY Opcion ORDER BY Opcion";

		System.out.println(callProcedureData);
		Query queryData = entityManager.createNativeQuery(callProcedureData);
		NativeQueryImpl nativeQueryData = (NativeQueryImpl) queryData;
		List<String> resultData = (List<String>) nativeQueryData.getResultList();

		return resultData;
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
		} else if (this.ORDENES == id) {
			return STORE_PROCEDURE_ORDENES;
		} else if (this.RENOVACION == id) {
			return STORE_PROCEDURE_RENOVACION;
		} else if (this.ESCRITURACION == id) {
			return STORE_PROCEDURE_ESCRITURACION;
		}

		return null;
	}

	private String processHeaderWithCalculations(String headers) {
		String[] div = headers.split("\n");
		String newHeader = "";
		for (int i = 0; i < div.length; i++) {
			div[i] = div[i].replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
			if (div[i].contains("=")) {
				String[] temp = div[i].split("=");
				if (temp.length > 1) {
					temp[1] = temp[1].replaceAll(",", ";");
					temp[1] = temp[1].trim();
					if (temp[1].charAt(temp[1].length() - 1) == ';') {
						temp[1] = temp[1].substring(0, temp[1].length() - 1);
					}
					div[i] = temp[0] + " = " + temp[1] + ",";
				}
			}
			newHeader += div[i];
		}
		return newHeader;

	}

	public String loadData(String data, int reportType) {
		System.out.println(data);
		JSONArray dataList = new JSONArray(data);

		try {
			String peticion = "";

			for (int i = 0; i < dataList.length(); i++) {
//				System.out.println("#### ID "+i);
				JSONObject element = dataList.getJSONObject(i);

				Map<String, Object> map = new HashMap<String, Object>();

				for (String entry : element.keySet()) {
//					System.out.println(entry);
					map.put(entry, element.get(entry) == null ? "IGNORE"
							: (element.get(entry).toString().equals("vacio")
									|| element.get(entry).toString().equals("Vacio")
									|| element.get(entry).toString().equals("VACIO")) ? null : element.get(entry));
				}
				String id = element.getString("UNI_ID");
				// System.out.println(id);

				String storeProcedureName = this.getStoreProcedureName(reportType);

				String entryInsert = "";
				if (reportType == this.LEGALIZACION) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[FechaSeguimientoCoordinador],[EstadoCoordinador],[ObservacionCoordinador],[FechaSeguimientoAnalista],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista],[FechaAsignacion],[AsignacionAnalista],[AnalistaVarado],[FechaAsignacionVarado],[FechaDesvarado] ) \n"
							+ "      VALUES (''" + map.get("UNI_ID") + "'',\n" + "      ''"
							+ map.get("FechaSeguimientoCoordinador") + "'',\n" + "      ''"
							+ map.get("EstadoCoordinador") + "'',\n" + "      ''" + map.get("ObservacionCoordinador")
							+ "'',\n" + "      ''" + map.get("FechaSeguimientoAnalista") + "'',\n" + "      ''"
							+ map.get("EstadoAnalista") + "'',\n" + "      ''" + map.get("ObservacionAnalista")
							+ "'',\n" + "      ''" + map.get("FechaAsignacion") + "'',\n" + "      ''"
							+ map.get("AsignacionAnalista") + "'',\n" + "      ''" + map.get("AnalistaVarado") + "'',\n"
							+ "      ''" + map.get("FechaAsignacionVarado") + "'',\n" + "      ''"
							+ map.get("FechaDesvarado") + "'')";
				} else if (reportType == this.PROMESA) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista] ) \n" + "      VALUES (''" + map.get("UNI_ID") + "'',\n"
							+ "      ''" + map.get("EstadoCoordinador") + "'',\n" + "      ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "      ''" + map.get("EstadoAnalista")
							+ "'',\n" + "      ''" + map.get("ObservacionAnalista") + "'')";
				} else if (reportType == this.SEGUIMIENTO_SUBSIDIO) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista] ) \n" + "      VALUES (''" + map.get("UNI_ID") + "'',\n"
							+ "      ''" + map.get("EstadoCoordinador") + "'',\n" + "      ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "      ''" + map.get("EstadoAnalista")
							+ "'',\n" + "      ''" + map.get("ObservacionAnalista") + "'')";
				} else if (reportType == this.ENTREGA) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[FechadeEntrega],[HoradeEntrega],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista], [Arquitecta],[CreacionCaso],[Apoderado]) \n"
							+ "      VALUES (''" + map.get("UNI_ID") + "'',\n" + "      ''" + map.get("FechadeEntrega")
							+ "'',\n" + "      ''" + map.get("HoradeEntrega") + "'',\n" + "      ''"
							+ map.get("EstadoCoordinador") + "'',\n" + "      ''" + map.get("ObservacionCoordinador")
							+ "'',\n" + "      ''" + map.get("EstadoAnalista") + "'',\n" + "      ''"
							+ map.get("ObservacionAnalista") + "'', " + "      ''" + map.get("Arquitecta") + "'', "
							+ "      ''" + map.get("CreacionCaso") + "'', " + "      ''" + map.get("Apoderado")
							+ "'' )";

				} else if (reportType == this.DESEMBOLSO) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[Comentarios],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista] ) \n" + "      VALUES (''" + map.get("UNI_ID") + "'',\n"
							+ "      ''" + map.get("EstadoCoordinador") + "'',\n" + "      ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "      ''" + map.get("Comentarios")
							+ "'',\n" + "      ''" + map.get("EstadoAnalista") + "'',\n" + "      ''"
							+ map.get("ObservacionAnalista") + "'')";
				} else if (reportType == this.TRAZABILIDAD) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista],[TipificacionCorreccionesCtl],[NBR],[AnalistaTrazabilidad],[DetalleObservacion] ) \n"
							+ "      VALUES (''" + map.get("UNI_ID") + "'',\n" + "      ''"
							+ map.get("EstadoCoordinador") + "'',\n" + "      ''" + map.get("ObservacionCoordinador")
							+ "'',\n" + "      ''" + map.get("EstadoAnalista") + "'',\n" + "      ''"
							+ map.get("ObservacionAnalista") + "'',\n" + "      ''"
							+ map.get("TipificacionCorreccionesCtl") + "'',\n" + "      ''" + map.get("NBR") + "'',\n"
							+ "      ''" + map.get("AnalistaTrazabilidad") + "'',\n" + "      ''"
							+ map.get("DetalleObservacion") + "'')";
				} else if (reportType == this.SUBSIDIO) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[Comentarios],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista] ) \n" + "      VALUES (''" + map.get("UNI_ID") + "'',\n"
							+ "      ''" + map.get("EstadoCoordinador") + "'',\n" + "      ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "      ''" + map.get("Comentarios")
							+ "'',\n" + "      ''" + map.get("EstadoAnalista") + "'',\n" + "      ''"
							+ map.get("ObservacionAnalista") + "'')";
				} else if (reportType == this.ORDENES) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "      ([UNI_ID],[Frpl],[QuienFirmaEsfp],[EstadoEsfpFrpl],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],[ObservacionAnalista],[EstadoAbogado],\n"
							+ "        [ObservacionAbogado],[CualFueElCambio],[NoPredialNacional],[NumeroId],[IdMunicipio],[RadicaciónOrdenPagoPySPredial],[FechaEstimadaDePyS],\n"
							+ "        [TipoPyS],[MunicipioPyS],[EstadoPyS],[EstadoOrdenes] ) \n" + "        VALUES (''"
							+ map.get("UNI_ID") + "'',\n" + "        ''" + map.get("Frpl") + "'',\n" + "        ''"
							+ map.get("QuienFirmaEsfp") + "'',\n" + "        ''" + map.get("EstadoEsfpFrpl") + "'',\n"
							+ "        ''" + map.get("EstadoCoordinador") + "'',\n" + "        ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "        ''" + map.get("EstadoAnalista")
							+ "'',\n" + "        ''" + map.get("ObservacionAnalista") + "'',\n" + "        ''"
							+ map.get("EstadoAbogado") + "'',\n" + "        ''" + map.get("ObservacionAbogado")
							+ "'',\n" + "        ''" + map.get("CualFueElCambio") + "'',\n" + "        ''"
							+ map.get("NoPredialNacional") + "'',\n" + "        ''" + map.get("NumeroId") + "'',\n"
							+ "        ''" + map.get("IdMunicipio") + "'',\n" + "        ''"
							+ map.get("RadicaciónOrdenPagoPySPredial") + "'',\n" + "        ''"
							+ map.get("FechaEstimadaDePyS") + "'',\n" + "        ''" + map.get("TipoPyS") + "'',\n"
							+ "        ''" + map.get("MunicipioPyS") + "'',\n" + "        ''" + map.get("EstadoPyS")
							+ "'',\n" + "        ''" + map.get("EstadoOrdenes") + "'')";
				} else if (reportType == this.RENOVACION) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "      ([UNI_ID],[FechaSeguimientoCoordinador],[EstadoCoordinador],[ObservacionCoordinador],[CampanasEspeciales],[CausalRenovacion],[FechaSeguimientoAnalista],[EstadoAnalista],[ObservacionAnalista],[EstadoPoderAnalista],[Asignacion],[Broker],[FechaAsignacion] ) \n"
							+ "        VALUES (''" + map.get("UNI_ID") + "'',\n" + "        ''"
							+ map.get("FechaSeguimientoCoordinador") + "'',\n" + "        ''"
							+ map.get("EstadoCoordinador") + "'',\n" + "        ''" + map.get("ObservacionCoordinador")
							+ "'',\n" + "        ''" + map.get("CampanasEspeciales") + "'',\n" + "        ''"
							+ map.get("CausalRenovacion") + "'',\n" + "        ''" + map.get("FechaSeguimientoAnalista")
							+ "'',\n" + "        ''" + map.get("EstadoAnalista") + "'',\n" + "        ''"
							+ map.get("ObservacionAnalista") + "'',\n" + "        ''" + map.get("EstadoPoderAnalista")
							+ "'',\n" + "        ''" + map.get("Asignacion") + "'',\n" + "        ''"
							+ map.get("Broker") + "'',\n" + "        ''" + map.get("FechaAsignacion") + "'')";
				} else if (reportType == this.ESCRITURACION) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "    ([UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],\n"
							+ "      [ObservacionAnalista] ) \n" + "      VALUES (''" + map.get("UNI_ID") + "'',\n"
							+ "      ''" + map.get("EstadoCoordinador") + "'',\n" + "      ''"
							+ map.get("ObservacionCoordinador") + "'',\n" + "      ''" + map.get("EstadoAnalista")
							+ "'',\n" + "      ''" + map.get("ObservacionAnalista") + "'')";
				} else if (reportType == this.RECAUDO) {
					entryInsert = "INSERT INTO #tblTEMP  \n"
							+ "      ( [UNI_ID],[EstadoCoordinador],[ObservacionCoordinador],[EstadoAnalista],[ObservacionAnalista],[FechaAcuerdoProximoPago],[MontoPactado],[LlamadaNo1],[LlamadaNo2],[LlamadaNo3],[LlamadaNo4],[LlamadaNo5],[EstadoEscriturado] ) \n"
							+ "        VALUES (''" + map.get("UNI_ID") + "'',\n" + "        ''"
							+ map.get("EstadoCoordinador") + "'',\n" + "        ''" + map.get("ObservacionCoordinador")
							+ "'',\n" + "        ''" + map.get("EstadoAnalista") + "'',\n" + "        ''"
							+ map.get("ObservacionAnalista") + "'',\n" + "        ''"
							+ map.get("FechaAcuerdoProximoPago") + "'',\n" + "        ''" + map.get("MontoPactado")
							+ "'',\n" + "        ''" + map.get("LlamadaNo1") + "'',\n" + "        ''"
							+ map.get("LlamadaNo2") + "'',\n" + "        ''" + map.get("LlamadaNo3") + "'',\n"
							+ "        ''" + map.get("LlamadaNo4") + "'',\n" + "        ''" + map.get("LlamadaNo5")
							+ "'',\n" + "        ''" + map.get("EstadoEscriturado") + "'')";
				}

				entryInsert = entryInsert.replace("Invalid Date", "null");
				entryInsert = entryInsert.replace("''null''", "NULL");
				entryInsert = entryInsert + " ";
				peticion = peticion + entryInsert;

			}

			peticion = peticion.replace("Invalid Date", "null");
			peticion = peticion.replace("Vacio", "null");
			peticion = peticion.replace("NULL", "null");
			peticion = peticion.replace("''null''", "NULL");

//			System.out.println(peticion);

			String callProcedureUpdate = "EXEC [dbo].[sp_load_data] @data = '" + peticion + "'" + " , @reportType = '"
					+ reportType + "'  ";

			Query queryDataUpdate = entityManager.createNativeQuery(callProcedureUpdate);
			NativeQueryImpl nativeQueryUpdate = (NativeQueryImpl) queryDataUpdate;
			String resultDataUpdate = (String) nativeQueryUpdate.getSingleResult();

			JSONObject resp = new JSONObject();
			resp.put("code", 0);
			resp.put("message", resultDataUpdate);

			return resp.toString();
		} catch (Exception e) {
			JSONObject resp = new JSONObject();
			resp.put("code", 1);
			resp.put("message", e.getMessage());
			return resp.toString();
		}

	}

	@Transactional
	public boolean auditData(String UNI_ID, String herramienta, String campo, String valorAnterior, String valorActual,
			String usuario) {

		if (valorAnterior == null || valorAnterior.equals("Invalid Date") || valorAnterior.equals("Vacio")
				|| valorAnterior.equals("NULL") || valorAnterior.equals("''null''")) {
			valorAnterior = null;
		}
		if (valorActual == null || valorActual.equals("Invalid Date") || valorActual.equals("Vacio")
				|| valorActual.equals("NULL") || valorActual.equals("''null''")) {
			valorActual = null;
		}

		String callProcedureDataSelected = "INSERT INTO [MacrosWebApp].[dbo].[tbl_auditoria] (UNI_ID,herramienta,campo, valor_anterior,valor_actual,usuario) VALUES('"
				+ UNI_ID + "', '" + herramienta + "', '" + campo + "', '" + valorAnterior + "', '" + valorActual
				+ "', '" + usuario + "')";

		try {
			Query queryDataSelected = entityManager.createNativeQuery(callProcedureDataSelected);
			NativeQueryImpl nativeQueryDataSelected = (NativeQueryImpl) queryDataSelected;
			int result = nativeQueryDataSelected.executeUpdate();
			if (result > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
