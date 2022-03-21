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
import org.springframework.transaction.annotation.Transactional;

import co.com.reportstools.models.User;

@Repository
public class LoginRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public boolean existUser(String userName) {

		String callProcedureDataSelected = "SELECT Id FROM [MacrosWebApp].[dbo].[tbl_users] WHERE UserName = '"
				+ userName + "' ";

		Query queryDataSelected = entityManager.createNativeQuery(callProcedureDataSelected);
		NativeQueryImpl nativeQueryDataSelected = (NativeQueryImpl) queryDataSelected;
		nativeQueryDataSelected.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Map<String, Object>> resultDataSelected = nativeQueryDataSelected.getResultList();

		if (resultDataSelected.size() > 0) {
			return true;
		}
		return false;
	}

	public String getUserList() {

		String callProcedureDataSelected = "SELECT tblUser.Id, UserName, tblRole.Name AS Role FROM [MacrosWebApp].[dbo].[tbl_users] tblUser "
				+ " LEFT JOIN [MacrosWebApp].[dbo].[tbl_user_roles] tblUserRol ON tblUserRol.UserId = tblUser.Id "
				+ " LEFT JOIN [MacrosWebApp].[dbo].[tbl_roles] tblRole ON tblRole.Id = tblUserRol.RoleId  ";

		Query queryDataSelected = entityManager.createNativeQuery(callProcedureDataSelected);
		NativeQueryImpl nativeQueryDataSelected = (NativeQueryImpl) queryDataSelected;
		nativeQueryDataSelected.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Map<String, Object>> resultDataSelected = (List<Map<String, Object>>) nativeQueryDataSelected
				.getResultList();

		JSONObject resultadoJSON = new JSONObject();
		resultadoJSON.put("data", resultDataSelected);
		resultadoJSON.put("code", 0);

		return resultadoJSON.toString();

	}

	public User getUser(String userName) {

		String callProcedureDataSelected = "SELECT TOP(1) Id, UserName FROM [MacrosWebApp].[dbo].[tbl_users] WHERE UserName = '"
				+ userName + "' ";

		Query queryDataSelected = entityManager.createNativeQuery(callProcedureDataSelected);
		NativeQueryImpl nativeQueryDataSelected = (NativeQueryImpl) queryDataSelected;
		nativeQueryDataSelected.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		Map<String, Object> resultDataSelected = (Map<String, Object>) nativeQueryDataSelected.getSingleResult();

		if (resultDataSelected != null) {
			User user = new User();
			user.setId(resultDataSelected.get("Id").toString());
			user.setUserName(resultDataSelected.get("UserName").toString());
			return user;
		}
		return null;
	}

	public String getRoles() {

		String callProcedureDataSelected = "SELECT * FROM [MacrosWebApp].[dbo].[tbl_roles] ";

		Query queryDataSelected = entityManager.createNativeQuery(callProcedureDataSelected);
		NativeQueryImpl nativeQueryDataSelected = (NativeQueryImpl) queryDataSelected;
		nativeQueryDataSelected.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Map<String, Object>> resultDataSelected = nativeQueryDataSelected.getResultList();

		JSONObject resultadoJSON = new JSONObject();
		resultadoJSON.put("data", resultDataSelected);
		resultadoJSON.put("code", 0);

		return resultadoJSON.toString();
	}

	public String getUserRole(String UserId) {

		String callProcedureDataSelected = "SELECT tRole.Name \n"
				+ "FROM [MacrosWebApp].[dbo].[tbl_user_roles] tUserRole\n"
				+ "INNER JOIN [MacrosWebApp].[dbo].[tbl_roles] tRole ON tRole.Id  = tUserRole.RoleId\n"
				+ "WHERE tUserRole.UserId  = '" + UserId + "' ";

		Query queryDataSelected = entityManager.createNativeQuery(callProcedureDataSelected);
		NativeQueryImpl nativeQueryDataSelected = (NativeQueryImpl) queryDataSelected;
		nativeQueryDataSelected.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Map<String, Object>> resultDataSelected = nativeQueryDataSelected.getResultList();

		if (resultDataSelected.size() > 0) {
			return resultDataSelected.get(0).get("Name").toString();
		}
		return null;
	}

	@Transactional
	public boolean createUser(String UUID, String userName, String passwordHash) {

		String normalizedUserName = userName.toUpperCase();
		String callProcedureDataSelected = "INSERT INTO [MacrosWebApp].[dbo].[tbl_users] "
				+ "(Id, UserName, NormalizedUserName, EmailConfirmed, PasswordHash, "
				+ "PhoneNumberConfirmed, TwoFactorEnabled, LockoutEnabled, AccessFailedCount) \n" + "VALUES('" + UUID
				+ "', '" + userName + "', '" + normalizedUserName + "', 0, '" + passwordHash + "', 0, 0, 1, 0) ";
		System.out.println(callProcedureDataSelected);

		Query queryDataSelected = entityManager.createNativeQuery(callProcedureDataSelected);
		NativeQueryImpl nativeQueryDataSelected = (NativeQueryImpl) queryDataSelected;
		int result = nativeQueryDataSelected.executeUpdate();
		if (result > 0) {
			return true;
		}
		return false;
	}

	@Transactional
	public boolean addOrUpdateRole(String userId, String roleId, int opt) {

		String callProcedureDataSelected = "INSERT INTO [MacrosWebApp].[dbo].[tbl_user_roles] (UserId, RoleId) VALUES('"
				+ userId + "', '" + roleId + "')";
		if (opt == 2) {
			callProcedureDataSelected = "UPDATE [MacrosWebApp].[dbo].[tbl_user_roles] SET RoleId='" + roleId
					+ "' WHERE UserId='" + userId + "' ";
		}
		System.out.println(callProcedureDataSelected);

		Query queryDataSelected = entityManager.createNativeQuery(callProcedureDataSelected);
		NativeQueryImpl nativeQueryDataSelected = (NativeQueryImpl) queryDataSelected;
		int result = nativeQueryDataSelected.executeUpdate();
		if (result > 0) {
			return true;
		}
		return false;
	}

}
