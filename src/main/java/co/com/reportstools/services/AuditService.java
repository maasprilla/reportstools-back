package co.com.reportstools.services;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.reportstools.repositories.AuditRepository;
import co.com.reportstools.repositories.GeneralRepository;
import co.com.reportstools.repositories.LoginRepository;

@Service
public class AuditService {

	@Autowired
	private AuditRepository auditRepository;

	public String getAudit(String startTime, String endTime) {
		return auditRepository.getAudit(startTime,endTime);
	}

}
