package net.glaso.ca.user;

import java.io.IOException;

import net.glaso.ca.business.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
		"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/sqlmap-context.xml",
		"file:src/test/resources/sqlmap-context.xml"
})
public class UserTest {

//	@Autowired
//    UserService userService;
	
	@Test
	public void rejectAppliedUser() throws JsonParseException, JsonMappingException, IOException {
//		userService.reigsterTest("{\"groupId\":\"dd\"}");
	}
	
}
