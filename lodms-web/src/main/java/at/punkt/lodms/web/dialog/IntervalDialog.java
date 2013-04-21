/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.dialog;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class IntervalDialog extends Window implements Dialog {

    private final VerticalLayout layout = new VerticalLayout();
    private final ComboBox cronPresets = new ComboBox("Interval");
    private final TextField cronExpression = new TextField("Cron Expression");
    private final Button configureButton = new Button("OK");
    private final CheckBox useExpression = new CheckBox("Custom");
    private DialogCloseHandler handler;

    public class CronPreset {

        private String expression;
        private String label;

        public CronPreset(String expression, String label) {
            this.expression = expression;
            this.label = label;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public IntervalDialog() {
        super("Configure Execution Interval");
        setModal(true);
        addComponent(layout);

        layout.addComponent(cronPresets);
        cronPresets.setImmediate(true);
        BeanItemContainer<CronPreset> presets = new BeanItemContainer<CronPreset>(CronPreset.class);
        CronPreset daily = new CronPreset("0 0 0 * * *", "Daily");
        presets.addBean(new CronPreset("0 * * * * *", "Every minute"));
        presets.addBean(new CronPreset("0 */30 * * * *", "Every 30 minutes"));
        presets.addBean(new CronPreset("0 0 * * * *", "Every hour"));
        presets.addBean(daily);
        presets.addBean(new CronPreset("0 0 0 * * MON", "First day of every week"));
        presets.addBean(new CronPreset("0 0 0 1 * *", "First day of every month"));
        cronPresets.setNullSelectionAllowed(false);
        cronPresets.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        cronPresets.setItemCaptionPropertyId("label");
        cronPresets.setContainerDataSource(presets);
        cronPresets.select(daily);

        layout.addComponent(useExpression);
        useExpression.setImmediate(true);
        useExpression.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Boolean checked = (Boolean) event.getProperty().getValue();
                if (checked) {
                    cronPresets.setEnabled(false);
                    cronExpression.setEnabled(true);
                } else {
                    cronPresets.setEnabled(true);
                    cronExpression.setEnabled(false);
                }
            }
        });
        useExpression.setImmediate(true);
        
        layout.addComponent(cronExpression);
        layout.addComponent(configureButton);
        cronExpression.setEnabled(false);
        final IntervalDialog intervalView = this;
        configureButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                handler.close();
                intervalView.close();
            }
        });
        center();
    }

    @Override
    public void setDialogCloseHandler(DialogCloseHandler handler) {
        this.handler = handler;
    }

    public String getCronExpression() {
        if (useExpression.booleanValue()) {
            return (String) cronExpression.getValue();
        } else {
            return ((CronPreset) cronPresets.getValue()).getExpression();
        }
    }
}