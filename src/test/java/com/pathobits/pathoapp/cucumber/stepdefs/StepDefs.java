package com.pathobits.pathoapp.cucumber.stepdefs;

import com.pathobits.pathoapp.KafkaelasticApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = KafkaelasticApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
