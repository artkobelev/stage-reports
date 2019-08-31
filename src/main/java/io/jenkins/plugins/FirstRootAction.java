package io.jenkins.plugins;

import java.util.Collection;
import java.util.Collections;

import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.support.visualization.table.FlowGraphTable;

import hudson.Extension;
import hudson.model.Action;
import jenkins.model.TransientActionFactory;

public final class FirstRootAction implements Action {

    public final WorkflowRun run;

    private FirstRootAction(WorkflowRun run) {
        this.run = run;
    }

    @Override
    public String getIconFileName() {
        return "gear.png";
    }

    @Override
    public String getDisplayName() {
        return "Stage Reports";
    }

    @Override
    public String getUrlName() {
        return "stageReports";
    }

    public StageGraphTable getFlowGraph() {
    	StageGraphTable t = new StageGraphTable(run.getExecution());
    	t.build();
        return t;
    }  

    @Extension
    public static final class Factory extends TransientActionFactory<WorkflowRun> {

        @Override public Class<WorkflowRun> type() {
            return WorkflowRun.class;
        }

        @Override public Collection<? extends Action> createFor(WorkflowRun run) {
            return Collections.singleton(new FirstRootAction(run));
        }

    }

}