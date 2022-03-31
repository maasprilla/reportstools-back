package co.com.reportstools.services;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapNameAware;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Service;

import co.com.reportstools.models.User;
import co.com.reportstools.repositories.LoginRepository;
import co.com.reportstools.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

@Service
public class LdapUserServiceImpl {

	@Autowired
	private LoginRepository loginRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public ResponseEntity<?> authenticate(String userDn, String credentials) {

//		boolean isAuthenticateLdap = this.authenticateLdap(userDn, credentials);
		boolean isAuthenticateLdap = true;
		if (isAuthenticateLdap) {
			String token = this.authenticationDB(userDn, credentials);
			JSONObject resp = new JSONObject();
			resp.put("code", 0);
			resp.put("data", token);
			resp.put("message", "");
			return ResponseEntity.ok(resp.toString());
		}

		JSONObject resp = new JSONObject();
		resp.put("code", 1);
		resp.put("message", "Credenciales incorrectas");
		return ResponseEntity.status(401).body(resp.toString());
	}

	private String authenticationDB(String userDn, String credentials) {
		boolean userCheck = this.loginRepository.existUser(userDn);

		if (!userCheck) {
			String hash = this.bytesToHex(credentials.getBytes());
			UUID uuid = UUID.randomUUID();
			boolean isCreated = loginRepository.createUser(uuid.toString(), userDn, hash);
			if(isCreated) {
				loginRepository.addOrUpdateRole(uuid.toString(), "c3ad1200-51bf-46ee-a889-0b5f01cfbd35", 1);
			}

		}

		return getToken(userDn);
	}

	private String getToken(String username) {
		User user = this.loginRepository.getUser(username);
		String role = this.loginRepository.getUserRole(user.getId());
		return this.jwtTokenUtil.generateToken(user.getUserName(), user.getId(), role);
	}

	private boolean authenticateLdap(String userDn, String credentials) {
		// service user
		String serviceUserDN = "Servicios TI";
		String serviceUserPassword = "M4Braz.bl18A";

		// user to authenticate
//		String identifyingAttribute = "uid";
		String identifyingAttribute = "sAMAccountName";
		String identifier = userDn;
		String password = credentials;
		String base = "OU=usuarios,OU=cali,OU=Constructora_Bolivar,DC=cbolivar,DC=corp";

		// LDAP connection info
		String ldap = "10.12.126.8";
		int port = 389;
		String ldapUrl = "ldap://" + ldap + ":" + port;

		// first create the service context
		DirContext serviceCtx = null;
		try {
			// use the service user to authenticate
			Properties serviceEnv = new Properties();
			serviceEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			serviceEnv.put(Context.PROVIDER_URL, ldapUrl);
			serviceEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			serviceEnv.put(Context.SECURITY_PRINCIPAL, serviceUserDN);
			serviceEnv.put(Context.SECURITY_CREDENTIALS, serviceUserPassword);
			System.out.println("inicia auth");
			serviceCtx = new InitialDirContext(serviceEnv);
			System.out.println("inicia auth paso 1");

			// we don't need all attributes, just let it get the identifying one
			String[] attributeFilter = { identifyingAttribute };
			SearchControls sc = new SearchControls();
			sc.setReturningAttributes(attributeFilter);
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

			System.out.println("serviceCtx " + new JSONObject(serviceCtx));

			// use a search filter to find only the user we want to authenticate
//			String searchFilter = "(" + identifyingAttribute + "=" + identifier + ")";
			String searchFilter = "(sAMAccountName={" + identifier + "})";
//			NamingEnumeration<SearchResult> results = serviceCtx.search(base, "(objectClass=user)", sc);
			NamingEnumeration<SearchResult> results = serviceCtx.search(base, "(sAMAccountName=" + identifier + ")",
					sc);
			System.out.println("result " + new JSONObject(results));
			System.out.println("hasMore " + results.hasMore());

			if (results.hasMore()) {
				System.out.println("hasMore " + results.hasMore());
				// get the users DN (distinguishedName) from the result
				SearchResult result = results.next();
				String distinguishedName = result.getNameInNamespace();
				System.out.println("inicia auth paso 2");
//				System.out.println("result "+new JSONObject(distinguishedName));

				// attempt another authentication, now with the user
				Properties authEnv = new Properties();
				authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
				authEnv.put(Context.PROVIDER_URL, ldapUrl);
				authEnv.put(Context.SECURITY_PRINCIPAL, distinguishedName);
				authEnv.put(Context.SECURITY_CREDENTIALS, password);
				new InitialDirContext(authEnv);

				System.out.println("Authentication successful");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (serviceCtx != null) {
				try {
					serviceCtx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
			}
		}
		System.err.println("Authentication failed");
		return false;
	}

	public String bytesToHex(byte[] bytes) {
		try {
			// getInstance() method is called with algorithm SHA-512
			MessageDigest md = MessageDigest.getInstance("SHA-512");

			// digest() method is called
			// to calculate message digest of the input string
			// returned as array of byte
			byte[] messageDigest = md.digest(bytes);

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);

			// Add preceding 0s to make it 32 bit
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}

			// return the HashText
			return hashtext;
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

}
