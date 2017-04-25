package com.sctrcd.buspassws;

import java.util.Arrays;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.sctrcd.buspassws.facts.BusPass;
import com.sctrcd.buspassws.facts.Person;

/**
 * The main class, which Spring Boot uses to bootstrap the application.
 *
 * @author Stephen Masters
 */
@SpringBootApplication
public class BusPassApp {

	private static Logger log = LoggerFactory.getLogger(BusPassApp.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(BusPassApp.class, args); 

/*        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);

        StringBuilder sb = new StringBuilder("Application beans:\n");
        for (String beanName : beanNames) {
            sb.append(beanName + "\n");
        }
        log.info(sb.toString());*/
        
        KieServices kieServices;
        KieContainer kieContainer;
        KieSession kieSession = null;
        
        kieServices = KieServices.Factory.get();
        kieContainer = kieServices.getKieClasspathContainer();
        kieSession = kieContainer.newKieSession("BusPassSession");
        Person person = new Person("Steve", 18);
        kieSession.insert(person);
        kieSession.fireAllRules();
        BusPass busPass = new BusPassService(kieContainer).findBusPass(kieSession);
        kieSession.dispose();
        log.info("Bus pass: " + busPass);
    }
    
    /**
     * By defining the {@link KieContainer} as a bean here, we ensure that
     * Drools will hunt out the kmodule.xml and rules on application startup.
     * Those can be found in <code>src/main/resources</code>.
     * 
     * @return The {@link KieContainer}.
     */
    @Bean
    public KieContainer kieContainer() {
        return KieServices.Factory.get().getKieClasspathContainer();
    }

}
