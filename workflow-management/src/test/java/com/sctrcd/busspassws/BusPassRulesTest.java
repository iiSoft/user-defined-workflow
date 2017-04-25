package com.sctrcd.busspassws;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.sctrcd.buspassws.BusPassService;
import com.sctrcd.buspassws.facts.BusPass;
import com.sctrcd.buspassws.facts.Person;

public class BusPassRulesTest {

    private KieServices kieServices;
    private KieContainer kieContainer;
    private KieSession kieSession;
    
    @Before
    public void initialize() {
        if (kieSession != null) {
            kieSession.dispose();
        }
        this.kieServices = KieServices.Factory.get();
        this.kieContainer = kieServices.getKieClasspathContainer();
        this.kieSession = kieContainer.newKieSession("BusPassSession");
    }

    /**
     * If this passes, then the {@link KieSession} was initialised and injected
     * into the Spring components.
     */
    @Test
    public void shouldConfigureDroolsComponents() {
        assertNotNull(kieSession);
    }
    
    @Test
    public void shouldDroolsFireRules() {
    	Person person = new Person("Steve", 18);
        kieSession.insert(person);
        kieSession.fireAllRules();
        BusPass busPass = new BusPassService(kieContainer).findBusPass(kieSession);
        kieSession.dispose();
        System.out.println("Bus pass: " + busPass);
    }

}
