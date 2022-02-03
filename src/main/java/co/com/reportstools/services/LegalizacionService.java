package co.com.reportstools.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.reportstools.repositories.LegalizacionRepository;

@Service
public class LegalizacionService {

	@Autowired
	private LegalizacionRepository legalizacionRepository;

	public String getStoreProcedureInfo(int page, int size, String sortFilter, String dataFilter) {
		return legalizacionRepository.getStoreProcedureInfo(page, size, sortFilter, dataFilter);
	}

	public List getStoreProcedureHeaderOptionList(String dataFilter, String dataGroup) {
		return legalizacionRepository.getStoreProcedureHeaderOptionList(dataFilter,dataGroup);
	}

}
