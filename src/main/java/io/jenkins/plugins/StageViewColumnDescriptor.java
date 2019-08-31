package io.jenkins.plugins;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

import org.jenkinsci.plugins.workflow.graph.FlowNode;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Descriptor;
import view.StageViewColumn;

public abstract class StageViewColumnDescriptor extends Descriptor<StageViewColumn> implements ExtensionPoint {
    protected StageViewColumnDescriptor(Class<? extends StageViewColumn> clazz) {
        super(clazz);
    }

    protected StageViewColumnDescriptor() {
    }

    /**
     * To enable rendering a table of {@link FlowNode} without the user explicitly configuring
     * columns, this method provides a default instance.
     *
     * If column requires some configuration and no sensible default instance exists, return null.
     *
     * When more columns get written, this concept will likely break down. Revisit this.
     *
     * @deprecated
     *      Don't use this method outside the core workflow plugins as we'll likely change this.
     */
    public @CheckForNull StageViewColumn getDefaultInstance() {
        return null;
    }

    public static ExtensionList<StageViewColumnDescriptor> all() {
        return ExtensionList.lookup(StageViewColumnDescriptor.class);
    }

    /**
     * @deprecated
     *      Don't use this method outside the core workflow plugins as we'll likely change this.
     */
    public static List<StageViewColumn> getDefaultInstances() {
        List<StageViewColumn> r = new ArrayList<StageViewColumn>();
        for (StageViewColumnDescriptor d : all()) {
        	StageViewColumn c = d.getDefaultInstance();
            if (c!=null)
                r.add(c);
        }
        return r;
    }
}