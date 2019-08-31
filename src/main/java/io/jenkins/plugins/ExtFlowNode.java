package io.jenkins.plugins;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jenkinsci.plugins.workflow.actions.ErrorAction;
import org.jenkinsci.plugins.workflow.actions.LogAction;
import org.jenkinsci.plugins.workflow.actions.TimingAction;
import org.jenkinsci.plugins.workflow.graph.FlowNode;

import hudson.model.Action;

public class ExtFlowNode implements Comparable<ExtFlowNode> {
	/* Указатель на узел-оригинал */
	private FlowNode node;
	/* Идентификатор узла */
	private long id = 0;
	/* Тип узла */
	private FlowNodeType type = null;
	/* Список указателей на дочерние узлы */
	private List <ExtFlowNode> childs = null;
	/* Список указателей на родительские узлы */
	private List <ExtFlowNode> parents = null;
	/* Список указателей всех узлов в графе */
	private List <ExtFlowNode> flowNodes = new ArrayList<ExtFlowNode>();
	/* Время старта узла */
	private long startTime = 0;
	/* Время выполнения узла */
	private long duration = 0;

	/**
	 * Конструктор класса.
	 * @param node узел графа
	 */
	public ExtFlowNode(FlowNode node) {
		this.node = node;
	}

	/**
	 * Получить объект-обертку из списка по узлу-оригиналу. Поиск узла-обертки
	 * осуществляется по равенству идентификаторов узлов.
	 * @return объект-обертка узла
	 */
	public static ExtFlowNode getExtNode(FlowNode node, List<ExtFlowNode> flowNodes) {
		long id = Long.valueOf(node.getId()).longValue();
		for (ExtFlowNode ext: flowNodes) {
			if (ext.getId() == id) {
				return ext;
			}
		}
		return null;
	}

	/**
	 * Получить все экшены узла (используются для получения дополнительной информации о узле).
	 * @return список экшенов
	 */
	public List<Action> getActions() {
		return node.getActions();
	}

	/**
	 * Получить дочерние узлы.
	 * @return дочерние узлы
	 */
	public List<ExtFlowNode> getChilds () {
		if (childs == null) {
			childs = new ArrayList<ExtFlowNode>();
			for (ExtFlowNode n: flowNodes) {
				List<ExtFlowNode> parents = n.getParents();
				for (ExtFlowNode p: parents) {
					if (p == this) {
						childs.add(n);
					}
				}
			}
		}
		return childs;
	}

	/**
	 * Получить имя узла, отображаемое в графе.
	 * @return имя узла
	 */
	public String getDisplayName() {
		return node.getDisplayName();
	}

	/**
	 * Получить время выполнения узла в миллисекундах.
	 * @return время выполнения узла
	 */
	public long getDuration() {
		if (duration == 0) {
			List <ExtFlowNode> childs = getChilds();
			long endTime = getStartTime();
			if (childs != null) {
				List<Long> startTimes = new ArrayList<>();
				for (ExtFlowNode node: childs) {
					startTimes.add(node.getStartTime());
				}
				endTime = Collections.max(startTimes);
			}
			duration = endTime - getStartTime();
		}
		return duration;
	}

	/**
	 * Получить ошибку, выброшенную при выполнении узла.
	 * @return ошибка узла; null - если ошибки нет
	 */
	public ErrorAction getError() {
		return node.getError();
	}

	/**
	 * Получить идентификатор узла.
	 * @return идентификатор узла
	 */
	public long getId() {
		if (id == 0) {
			id = Long.valueOf(node.getId()).longValue();
		}
		return id;
	}

	/**
	 * Получить лог узла в строковои формате.
	 * @return лог узла
	 * @throws IOException 
	 */
	public String getLogText() throws IOException {
		LogAction la = node.getAction(LogAction.class);
		if (la != null) {
			StringWriter w = new StringWriter();
			la.getLogText().writeLogTo(0, w);
			return w.toString();
		}
		return null;
	}

	/**
	 * Получить узел.
	 * @return указатель на узел
	 */
	public FlowNode getNode() {
		return node;
	}

	/**
	 * Получить родительские узлы.
	 * @return родительские узлы
	 */
	public List<ExtFlowNode> getParents() {
		if (parents == null) {
			parents = new ArrayList<ExtFlowNode>();
			List<FlowNode> nodeParents = node.getParents();
			for (FlowNode n: nodeParents) {
				parents.add(ExtFlowNode.getExtNode(n, flowNodes));
			}
		}
		return parents;
	}

	/**
	 * Получить время запуска узла в миллисекундах.
	 * @return время запуска узла
	 */
	public long getStartTime() {
		if (startTime == 0) {
			startTime = TimingAction.getStartTime(node);
		}
		return startTime;
	}

	/**
	 * Получить тип узла.
	 * @see FlowNodeType
	 * @return тип узла
	 */
	public FlowNodeType getType() {
		if (type == null) {
			String displayName = getDisplayName();

			//TODOAT научиться определять тип узла нормальным способом
			if (displayName.contains("Stage : Start")) {
				type = FlowNodeType.STAGE_START;
			}
			else if (displayName.contains("Stage : End")) {
				type = FlowNodeType.STAGE_END;
			}
			else {
				type = FlowNodeType.OTHER;
			}
		}
		return type;
	}

	/**
	 * Установить узлы графа. Перед работой с узлом вызвать метод, 
	 * для определения родительских и дочерних узлов.
	 */
	public void setNodes(List<ExtFlowNode> flowNodes) {
		this.flowNodes = flowNodes;
	}

	@Override
	public int compareTo(ExtFlowNode o2) {
		long diff = getId() - o2.getId();
		if (diff < 0) return -1;
		else if (diff > 0) return 1;
		
		return 0;
	}
}
