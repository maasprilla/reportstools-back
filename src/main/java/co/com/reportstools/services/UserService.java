package co.com.reportstools.services;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.com.reportstools.repositories.GeneralRepository;
import co.com.reportstools.repositories.LoginRepository;

@Service
public class UserService {

	@Autowired
	private LoginRepository loginRepository;

	public String getUsers() {
		return loginRepository.getUserList();
	}

	public String getRoles() {
		return loginRepository.getRoles();
	}

	public String addOrUpdateRole(String userId, String roleId) {
		Boolean result = loginRepository.addOrUpdateRole(userId, roleId, 2);
		JSONObject resultadoJSON = new JSONObject();
		resultadoJSON.put("code", result?0:1);
		return resultadoJSON.toString();

	}

}
