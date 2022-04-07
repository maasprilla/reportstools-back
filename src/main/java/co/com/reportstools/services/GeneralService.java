package co.com.reportstools.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.reportstools.repositories.GeneralRepository;

@Service
public class GeneralService {

	@Autowired
	private GeneralRepository generalRepository;

	public String getProyectos() {
		return generalRepository.getProyectos();
	}
	
	public String updateProject(String pryId, String pryNombre, int opt) {
		return generalRepository.updateProject(pryId, pryNombre, opt);
	}

}
