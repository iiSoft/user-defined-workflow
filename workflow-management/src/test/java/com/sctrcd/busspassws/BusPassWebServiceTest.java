package com.sctrcd.busspassws;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sctrcd.buspassws.BusPassApp;
import com.sctrcd.buspassws.BusPassService;
import com.sctrcd.buspassws.facts.AdultBusPass;
import com.sctrcd.buspassws.facts.BusPass;
import com.sctrcd.buspassws.facts.ChildBusPass;
import com.sctrcd.buspassws.facts.Person;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(SpringRunner.class)
public class BusPassWebServiceTest {

    @Autowired
    private BusPassService busPassService;

    @Test
    public void shouldIssueAdultBusPass() {
        Person person = new Person("Steve", 16);
        BusPass busPass = busPassService.getBusPass(person);
        
        System.out.println("Bus pass: " + busPass);
        
        assertEquals(AdultBusPass.class, busPass.getClass());
    }
    
    @Test
    public void shouldIssueChildBusPass() {
        Person person = new Person("Steve", 15);
        BusPass busPass = busPassService.getBusPass(person);
        
        System.out.println("Bus pass: " + busPass);
        
        assertEquals(ChildBusPass.class, busPass.getClass());
    }

}
