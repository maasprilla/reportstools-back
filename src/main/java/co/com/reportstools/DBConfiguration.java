package co.com.reportstools;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DBConfiguration {

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		dataSource.setUrl("jdbc:sqlserver://dwh.cbolivar.com:13144;databaseName=MacrosWebApp");
		dataSource.setUsername("macroswebapp");
		dataSource.setPassword("M4cr0sw3b4pp*2022");
		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan("co");
		Properties hibernateProperties = new Properties();
		hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
		hibernateProperties.put("hibernate.show_sql", true);
		hibernateProperties.put("hibernate.hbm2ddl.auto", "none");
		sessionFactory.setHibernateProperties(hibernateProperties);

		return sessionFactory;
	}

//	@Bean
//	public HibernateTransactionManager transactionManager() {
//		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
//		transactionManager.setSessionFactory(sessionFactory().getObject());
//		return transactionManager;
//	}
}