package io.jenkins.plugins;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.workflow.flow.FlowExecution;

public class StageGraphTable {
	private FlowExecution execution;
	private List<StageReport> stages;
	
	public StageGraphTable(FlowExecution execution) {
		this.execution = execution;
		stages = new ArrayList<StageReport>();
		stages.add(new StageReport("War"));
		stages.add(new StageReport("Selenium"));
	}
	
	public List<StageReport> getStages() {
		return stages;
	}
	
	public void build() {
		FlowNodeScanner scanner = new FlowNodeScanner(execution);
		scanner.scan();
		stages = scanner.getStagesInfo();
	}
}
