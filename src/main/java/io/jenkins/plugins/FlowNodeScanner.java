package io.jenkins.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.workflow.flow.FlowExecution;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.graphanalysis.DepthFirstScanner;

public class FlowNodeScanner {
	/* Сканер для поиска всех узлов в графе */
	private DepthFirstScanner scanner = new DepthFirstScanner();
	/* Список указателей всех узлов в графе */
	private List<ExtFlowNode> extFlowNodes = new ArrayList<ExtFlowNode>();
	/* Результаты сканирования графа */
	private List<StageReport> stages = new ArrayList<StageReport>();
	
	private static final Logger LOGGER = Logger.getLogger(FlowNodeScanner.class.getName()); 

	/**
	 * Конструктор класса.
	 * @see FlowExecution
	 * @param execution объект выполнения сборки
	 */
	public FlowNodeScanner(FlowExecution execution) {
		List<FlowNode> flowNodes = scanner.allNodes(execution);

		for (FlowNode node: flowNodes) {
			extFlowNodes.add(new ExtFlowNode(node));
		}

		for (ExtFlowNode extNode: extFlowNodes) {
			extNode.setNodes(extFlowNodes);
		}
	}

	/**
	 * Найти объект с результатами этапа. Если такого объекта нет - создать.
	 * @param stageName имя этапа
	 * @return объект с результатами этапа
	 */
	private StageReport findOrCreate(String stageName) {
		StageReport stage = null;
		
		for (StageReport st: stages) {
			if (st.getName().equals(stageName)) {
				stage = st;
				break;
			}
		}
					
		if (stage == null) {
			stage = new StageReport(stageName);
			stages.add(stage);
		}
		return stage;
	}

	/**
	 * Получить первый узел этапа, к которому принадлежит узел.
	 * @param node узел
	 * @return узел старта этапа
	 */
	private ExtFlowNode getParentStageNode (ExtFlowNode node) {
		if (node.getType() == FlowNodeType.STAGE_START) {
			return node;
		}

		ExtFlowNode current = node;
		int stageBlockCounter = 0;

		while (true) {
			List<ExtFlowNode> parents = current.getParents();
			if (parents == null || parents.isEmpty()) break;
			ExtFlowNode p = parents.get(0);

			if (p.getType() == FlowNodeType.STAGE_END) {
				stageBlockCounter--;
			}
			else if (p.getType() == FlowNodeType.STAGE_START) {
				if (stageBlockCounter == 0) {
					return p;
				}
				stageBlockCounter++;
			}
			current = p;
		}
		return null;
	}

	/**
	 * Вернуть сканер.
	 */
	public DepthFirstScanner getScanner() {
		return this.scanner;
	}

	/**
	 * Результаты сканирования графа.
	 * @return результаты сканирования графа
	 */
	public List<StageReport> getStagesInfo() {
		return this.stages;
	}

	/**
	 * Сканировать граф.
	 */
	public void scan() {
		for (ExtFlowNode node: extFlowNodes) {
			ExtFlowNode stageNode = getParentStageNode(node);
			if (stageNode != null) {
				findOrCreate(stageNode.getChilds().get(0).getDisplayName()).addNode(node);
			}
		}
	}
}