package co.com.reportstools.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.reportstools.repositories.LegalizacionRepository;

@Service
public class LegalizacionService {

	@Autowired
	private LegalizacionRepository legalizacionRepository;

	public String getStoreProcedureInfo(int page, int size) {
		return legalizacionRepository.getStoreProcedureInfo(page, size);
	}

}
