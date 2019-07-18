package net.glaso.ca.user.scheduler;

import net.glaso.ca.business.scheduler.service.CaUserAuthInfoScheduler;
import net.glaso.ca.framework.init.CaSettings;
import net.glaso.ca.framework.init.MailSettings;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.IOException;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml",
        "file:src/main/webapp/WEB-INF/spring/root-context.xml",
        "file:src/main/webapp/WEB-INF/spring/sqlmap-context.xml",
        "file:src/test/resources/sqlmap-context.xml"
})
public class SchedulerTest {

    @Inject
    CaUserAuthInfoScheduler scheduler;

    @BeforeClass
    public static void init() throws IOException {
        CaSettings.init();

        MailSettings.init();
    }

    @Test
    public void sample001() {
        scheduler.runner().run();
    }
}
