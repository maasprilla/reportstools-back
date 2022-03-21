package co.com.reportstools;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class Ldap {
	
	@Bean
	public BaseLdapPathContextSource contextSource() {
		LdapContextSource bean = new LdapContextSource();
		bean.setUrl("ldap://10.12.126.8:389");
		bean.setBase("OU=usuarios,OU=cali,OU=Constructora_Bolivar,DC=cbolivar,DC=corp");
		// instead of this i want to put here the username and password provided by the
		// user
		bean.setUserDn("10.12.126.8\\Servicios TI");
		bean.setPassword("M4Braz.bl18A");
		bean.setPooled(true);
		bean.setReferral("follow");
		bean.afterPropertiesSet();
		return bean;
	}
	
	  @Bean
	  public LdapTemplate ldapTemplate() {
	      LdapTemplate template = new LdapTemplate(contextSource());
	      return template;
	  }

}
