package org.wso2.appserver.sample.ee.jsf.ejb;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class Person {

    @Inject
    private Counter counter;

    @EJB
    private Analyzer analyzer;

    private String name;
    private String analysisResults;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnalysisResults() {
        return analysisResults;
    }

    public void setAnalysisResults(String analysisResults) {
        this.analysisResults = analysisResults;
    }

    public String enterPerson() {
        counter.increaseCount();
        analysisResults = analyzeName();
        return "success";
    }

    public String analyzeName() {
        return analyzer.analyzeName(name);
    }


}
