package view;

import org.kohsuke.stapler.export.Exported;

import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import io.jenkins.plugins.StageViewColumnDescriptor;

public class StageViewColumn extends AbstractDescribableImpl<StageViewColumn> implements ExtensionPoint {
    /**
     * Returns the name of the column that explains what this column means
     *
     * @return
     *      The convention is to use capitalization like "Foo Bar Zot".
     */
    @Exported
    public String getColumnCaption() {
        return getDescriptor().getDisplayName();
    }

    public StageViewColumnDescriptor getDescriptor() {
        return (StageViewColumnDescriptor)super.getDescriptor();
    }
}