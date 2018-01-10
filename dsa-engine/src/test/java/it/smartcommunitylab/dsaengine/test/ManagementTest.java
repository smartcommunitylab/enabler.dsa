package it.smartcommunitylab.dsaengine.test;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import it.smartcommunitylab.dsaengine.controller.ManagementController;
import it.smartcommunitylab.dsaengine.model.BaseDataSetConf;
import it.smartcommunitylab.dsaengine.model.ConfigurationProperties;
import it.smartcommunitylab.dsaengine.model.DataSetConf;
import it.smartcommunitylab.dsaengine.model.Manager;
import it.smartcommunitylab.dsaengine.model.User;
import it.smartcommunitylab.dsaengine.utils.AuthManager;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ManagementTest {

	@Autowired
	private WebApplicationContext wac;	
	
	@MockBean
	private AuthManager authManager;
	
	private MockMvc mocker;
	private ObjectMapper mapper;	

	private static final String ROOT = "/management/";
	
	private static final String DOMAIN = "test_domain";
	private static final String DATASET = "test_dataset";
	private static final String MANAGER = "test_manager";
	private static final String USER = "test_user";
	
	@PostConstruct
	public void init() throws Exception {
		mocker = MockMvcBuilders.webAppContextSetup(wac).build();
		
		MockitoAnnotations.initMocks(this);
		mapper = new ObjectMapper();
	}	
	
	@Test
	public void test() throws Exception {
		when(authManager.getAuthToken(anyObject())).thenReturn(" ");
		when(authManager.getUsername(anyObject())).thenReturn("admin");
		when(authManager.checkRole(anyString(), anyObject())).thenCallRealMethod();
		when(authManager.getRoles(anyString())).thenReturn(Lists.newArrayList(ManagementController.DSA_PROVIDER_ROLE_PREFIX + DOMAIN));
		
		// Domain
		
		String domainEndpoint = ROOT + DOMAIN + "/domains";
		String datasetEndpoint = ROOT + DOMAIN + "/datasets";
		String datasetId = null;
		
//		BaseDomainConf bdc = new BaseDomainConf();
		ConfigurationProperties props = new ConfigurationProperties();
		props.setArchivePolicy("ap");
		props.setClients(Collections.EMPTY_LIST);
		props.setDataMapping(Collections.EMPTY_MAP);
		props.setIndexFormat("if");
//		bdc.setDefaultConfigurationProperties(props);
		
		RequestBuilder builder = null;
		String result = null;
		
		try {
		builder = MockMvcRequestBuilders.post(domainEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", ""); // .content(mapper.writeValueAsString(bdc));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("CREATE DOMAIN");
		System.err.println(result);
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(400)).andReturn().getResponse().getContentAsString();
		System.err.println("CREATE DOMAIN DUP");
		System.err.println(result);
		
//		props.setIndexFormat("if2");
//		builder = MockMvcRequestBuilders.put(domainEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(bdc));
//		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
//		System.err.println("UPDATE DOMAIN");
//		System.err.println(result);		
//		DomainConf domain2 = mapper.readValue(result, DomainConf.class);
//		Assert.assertEquals("if2", domain2.getDefaultConfigurationProperties().getIndexFormat());

		builder = MockMvcRequestBuilders.get(domainEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "");
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("GET DOMAIN");
		System.err.println(result);		
		
		// Manager
		
		String managerEndpoint = ROOT + DOMAIN + "/managers";
		Manager manager = new Manager();
		manager.setUsername(MANAGER);
		
		builder = MockMvcRequestBuilders.post(managerEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(manager));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("CREATE MANAGER");
		System.err.println(result);		
		
		Manager manager2 =  mapper.readValue(result, Manager.class);

		builder = MockMvcRequestBuilders.get(managerEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(manager));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("GET MANAGERS");
		System.err.println(result);	
		Assert.assertEquals(2, mapper.readValue(result, List.class).size());
		
		builder = MockMvcRequestBuilders.get(managerEndpoint + "/" + manager2.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(manager));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("GET MANAGER");
		System.err.println(result);			
		
//		props.setIndexFormat("if3");
//		builder = MockMvcRequestBuilders.put(domainEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(bdc));
//		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
//		System.err.println("UPDATE DOMAIN");
//		System.err.println(result);		
//		domain2 = mapper.readValue(result, DomainConf.class);
//		Assert.assertEquals(2, domain2.getManagers().size());		
		
		builder = MockMvcRequestBuilders.delete(managerEndpoint + "/" + manager2.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(manager));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("DELETE MANAGER");
		System.err.println(result);				
		
		builder = MockMvcRequestBuilders.get(managerEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(manager));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("GET MANAGERS");
		Assert.assertEquals(1, mapper.readValue(result, List.class).size());
		System.err.println(result);				
		
		// User
		
		String userEndpoint = ROOT + DOMAIN + "/users";
		User user = new User();
		user.setUsername(USER);
		user.setDataset(DATASET);		
		
		builder = MockMvcRequestBuilders.post(userEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(user));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("CREATE USER");
		System.err.println(result);			
		
		User user2 =  mapper.readValue(result, User.class);

		builder = MockMvcRequestBuilders.get(userEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(manager));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("GET USERS");
		System.err.println(result);	
		Assert.assertEquals(1, mapper.readValue(result, List.class).size());
		
		builder = MockMvcRequestBuilders.get(userEndpoint + "/" + user2.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(manager));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("GET USER");
		System.err.println(result);		
		
//		props.setIndexFormat("if4");
//		builder = MockMvcRequestBuilders.put(domainEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(bdc));
//		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
//		System.err.println("UPDATE DOMAIN");
//		System.err.println(result);		
//		domain2 = mapper.readValue(result, DomainConf.class);
//		Assert.assertEquals(1, domain2.getUsers().size());		
		
		builder = MockMvcRequestBuilders.delete(userEndpoint + "/" + user2.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(user));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("DELETE USER");
		System.err.println(result);			
		
		builder = MockMvcRequestBuilders.get(userEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(manager));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("GET USERS");
		System.err.println(result);		
		Assert.assertEquals(0, mapper.readValue(result, List.class).size());
		
		// Dataset
		
		BaseDataSetConf bdsc = new BaseDataSetConf();
		bdsc.setConfigurationProperties(props);
		bdsc.setDataset(DATASET);
		
		builder = MockMvcRequestBuilders.post(datasetEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(bdsc));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("CREATE DATASET");
		System.err.println(result);	
		
		DataSetConf dsc = mapper.readValue(result, DataSetConf.class);
		datasetId = dsc.getId();
		
		bdsc.setConfigurationProperties(props);
		props.setIndexFormat("if5");
		builder = MockMvcRequestBuilders.put(datasetEndpoint + "/" + dsc.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "").content(mapper.writeValueAsString(bdsc));
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("UPDATE DATASET");
		System.err.println(result);	
		dsc =  mapper.readValue(result, DataSetConf.class);
		Assert.assertEquals("if5", dsc.getConfigurationProperties().getIndexFormat());
		} finally {
		// Cleanup
		
		if (datasetId != null) {
			builder = MockMvcRequestBuilders.delete(datasetEndpoint + "/" + datasetId).contentType(MediaType.APPLICATION_JSON).header("Authorization", "");
			result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
			System.err.println("DELETE DATASET");
			System.err.println(result);
			result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(400)).andReturn().getResponse().getContentAsString();
			System.err.println("DELETE DATASET DUP");
			System.err.println(result);
		}
		
		
		builder = MockMvcRequestBuilders.delete(domainEndpoint).contentType(MediaType.APPLICATION_JSON).header("Authorization", "");
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(200)).andReturn().getResponse().getContentAsString();
		System.err.println("DELETE DOMAIN");
		System.err.println(result);
		result = mocker.perform(builder).andExpect(MockMvcResultMatchers.status().is(400)).andReturn().getResponse().getContentAsString();
		System.err.println("DELETE DOMAIN DUP");
		System.err.println(result);		
		}
	}
	
}

