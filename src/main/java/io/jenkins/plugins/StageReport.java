package io.jenkins.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import org.jenkinsci.plugins.pipeline.StageStatus;
import org.jenkinsci.plugins.workflow.actions.ErrorAction;
import org.jenkinsci.plugins.workflow.actions.TagsAction;

import hudson.model.Action;

public class StageReport {
	/* Список указателей узлов в графе для этапа */
	private List<ExtFlowNode> flowNodes = new ArrayList<ExtFlowNode>();
	/* Имя этапа */
	private String name;
	/* Список ошибок этапа */
	private List<ErrorAction> errors = null;

	/**
	 * Конструктор класса.
	 * @param name имя этапа
	 */
	public StageReport(String name) {
		this.name = name;
	}

	/**
	 * Добавить узел этапа.
	 * @param node узел
	 */
	public void addNode(ExtFlowNode node) {
		flowNodes.add(node);
	}

	/**
	 * Получить время выполнения этапа в миллисекундах.
	 * @return время выполнения узла
	 */
	public long getDuration() {
		return getNodes().stream().mapToLong(ExtFlowNode::getDuration).sum();
	}

	/**
	 * Получить время окончания этапа в миллисекундах.
	 * @return время окончания этапа
	 */
	public long getEndTime() {
		return getTail().getStartTime();
	}
	
	public String getStatus() {
		if (getError().isEmpty()) {
			return "SUCCESS";
		}
		return "FAILURE";
	}

	/**
	 * Получить ошибки, выброшенные при выполнении этапа.
	 * @return ошибки этапа
	 */
	public List<ErrorAction> getError() {
		if (errors == null) {
			errors = new ArrayList<ErrorAction>();
			Collections.sort(flowNodes);
			for (ExtFlowNode node: flowNodes) {
				ErrorAction error = node.getError();
				if (error != null) {
					errors.add(error);
				}
			}
		}
		return errors;
	}

	/**
	 * Получить указатель на заголовок этапа.
	 * @return заголовок этапа
	 */
	public ExtFlowNode getFirst() {
		return getHead().getChilds().get(0);
	}

	/**
	 * Получить указатель на голову этапа.
	 * @return узел начала этапа
	 */
	public ExtFlowNode getHead() {
		return Collections.min(flowNodes);
	}

	/**
	 * Получить лог этапа в строковои формате.
	 * @return лог этапа
	 * @throws IOException 
	 */
	public String getLogText() throws IOException {
		StringBuffer buf = new StringBuffer();

		Collections.sort(flowNodes);
		for (ExtFlowNode node: flowNodes) {
			String flowLog = node.getLogText();
			if (flowLog != null && !flowLog.isEmpty()) {
				buf.append(flowLog);
			}
		}
		return buf.toString();
	}
	
	public void createLog()
	{
		
	}

	/**
	 * Получить имя этапа.
	 * @return имя этапа
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Получить узлы этапа.
	 * @return узлы этапа
	 */
	public List<ExtFlowNode> getNodes() {
		return flowNodes;
	}

	/**
	 * Получить время запуска этапа в миллисекундах.
	 * @return время запуска этапа
	 */
	public long getStartTime() {
		return getHead().getStartTime();
	}

	/**
	 * Получить указатель на хвост этапа.
	 * @return узел конца этапа
	 */
	public ExtFlowNode getTail() {
		return Collections.max(flowNodes);
	}

	/**
	 * Проверить, пропущен ли этап.
	 * @return true, если этап пропущен; false - иначе
	 */
//	public boolean isSkipped() {
//		ExtFlowNode node = getFirst();
//
//		for (Action action : node.getActions()) {
//			if (action instanceof TagsAction && ((TagsAction) action).getTagValue(StageStatus.TAG_NAME) != null) {
//				TagsAction tagsAction = (TagsAction) action;
//				String value = tagsAction.getTagValue(StageStatus.TAG_NAME);
//				return value != null && value.equals(StageStatus.getSkippedForConditional());
//			}
//		}
//		return false;
//	}
}
