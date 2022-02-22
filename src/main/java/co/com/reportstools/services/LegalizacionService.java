package co.com.reportstools.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.reportstools.repositories.LegalizacionRepository;

@Service
public class LegalizacionService {

	@Autowired
	private LegalizacionRepository legalizacionRepository;

	public String getStoreProcedureInfo(String obras, int page, int size, String sortFilter, String dataFilter,
			int reportType) {
		return legalizacionRepository.getStoreProcedureInfo(obras, page, size, sortFilter, dataFilter, reportType);
	}

	public List getStoreProcedureHeaderOptionList(String obras, String dataFilter, String dataGroup, int reportType) {
		return legalizacionRepository.getStoreProcedureHeaderOptionList(obras, dataFilter, dataGroup, reportType);
	}

	public List getStoreProcedureHeaderOptionFilterList(String obras, String dataFilter, String dataGroup,
			String filterLike, int reportType) {
		return legalizacionRepository.getStoreProcedureHeaderOptionFilterList(obras, dataFilter, dataGroup, filterLike,
				reportType);
	}

	public String updateLegalizacion(String data, int reportType) {
		return legalizacionRepository.updateLegalizacion(data, reportType);
	}

	public String multipleUpdateLegalizacion(String field, String newValue, String obras, String sortFilter,
			String dataFilter, int reportType) {
		return legalizacionRepository.multipleUpdateLegalizacion(field, newValue, obras, sortFilter, dataFilter,
				reportType);
	}

	public List getOptionListModal(String herramienta, String campo) {
		return legalizacionRepository.getOptionListModal(herramienta, campo);
	}

}
