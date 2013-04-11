/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.web.view;

import at.punkt.lodms.Disableable;
import at.punkt.lodms.integration.*;
import at.punkt.lodms.web.ConfigSuccessHandler;
import at.punkt.lodms.web.LodmsApplication;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alex Kreiser
 */
public class ConfiguredComponentTable<T> extends Table {

    private Map<T, List<Component>> componentMap = new HashMap<T, List<Component>>();
    
    private void addComponent(T id, Component component) {
        if (!componentMap.containsKey(id)) {
            componentMap.put(id, new ArrayList<Component>());
        }
        componentMap.get(id).add(component);
    }

    public ConfiguredComponentTable(final LodmsApplication application, Class<T> type, final List<T> jobComponents, final BeanItemContainer<T> components, boolean configure, boolean delete) {
        super("Selected " + type.getSimpleName() + "s");

        setContainerDataSource(components);
        setSizeFull();
        setSelectable(true);
        setVisibleColumns(new String[]{});
        
        addGeneratedColumn("Selected " + type.getSimpleName() + "s", new Table.ColumnGenerator() {

            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                String label = itemId.getClass().getSimpleName();
                if (itemId instanceof UIComponent) {
                    label = ((UIComponent) itemId).asString();
                }
                Label labelComp = new Label(label);
                addComponent((T) itemId, labelComp);
                if (itemId instanceof Disableable && ((Disableable) itemId).isDisabled()) {
                    labelComp.setEnabled(false);
                }
                return labelComp;
            }
        });
        if (configure) {
            addGeneratedColumn("Configure", new Table.ColumnGenerator() {

                @Override
                public Component generateCell(Table source, final Object itemId, Object columnId) {
                    if (itemId instanceof Configurable) {
                        Button configure = new Button("Configure", new Button.ClickListener() {

                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                if (itemId instanceof ConfigDialogProvider) {
                                    application.displayConfigWindow((ConfigDialogProvider) itemId, new ConfigSuccessHandler() {

                                        @Override
                                        public void configured() {
                                        }
                                    }, ((Configurable) itemId).getConfig());
                                } else if (itemId instanceof ConfigBeanProvider) {
                                    application.displayConfigWindow((ConfigBeanProvider) itemId, new ConfigSuccessHandler() {

                                        @Override
                                        public void configured() {
                                        }
                                    }, ((Configurable) itemId).getConfig());
                                }
                            }
                        });
                        addComponent((T) itemId, configure);
                        if (itemId instanceof Disableable && ((Disableable) itemId).isDisabled()) {
                            configure.setEnabled(false);
                        }
                        return configure;
                    } else {
                        return null;
                    }
                }
            });
            setColumnWidth("Configure", 100);
        }
        if (delete) {
            addGeneratedColumn("Disable", new Table.ColumnGenerator() {

                @Override
                public Component generateCell(final Table source, final Object itemId, Object columnId) {
                    if (itemId instanceof Disableable) {
                        boolean disabled = ((Disableable) itemId).isDisabled();
                        String caption = disabled ? "enable" : "disable";
                        final Button disable = new Button(caption);
                        disable.addListener(new Button.ClickListener() {

                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                if (((Disableable) itemId).isDisabled()) {
                                    ((Disableable) itemId).setDisabled(false);
                                    disable.setCaption("disable");
                                    source.requestRepaint();
                                    for (Component c : componentMap.get((T) itemId)) {
                                        c.setEnabled(true);
                                    }
                                } else {
                                    ((Disableable) itemId).setDisabled(true);
                                    disable.setCaption("enable");
                                    source.requestRepaint();
                                    for (Component c : componentMap.get((T) itemId)) {
                                        c.setEnabled(false);
                                    }
                                }
                            }
                        });
                        return disable;
                    } else {
                        return null;
                    }
                }
            });
            setColumnWidth("Disable", 70);
            addGeneratedColumn("Order", new Table.ColumnGenerator() {

                @Override
                public Component generateCell(Table source, final Object itemId, Object columnId) {
                    HorizontalLayout buttons = new HorizontalLayout();
                    final int index = jobComponents.indexOf((T) itemId);
                    Button up = new Button("", new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            if (index > 0) {
                                Collections.swap(jobComponents, index, index - 1);
                                components.sort(new Object[]{}, new boolean[]{});
                            }
                        }
                    });
                    if (index <= 0) {
                        up.setEnabled(false);
                    }
                    up.setStyleName(Reindeer.BUTTON_LINK);
                    up.setIcon(new ThemeResource("../runo/icons/32/arrow-up.png"));

                    Button down = new Button("", new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            if (index < jobComponents.size() - 1) {
                                Collections.swap(jobComponents, index, index + 1);
                                components.sort(new Object[]{}, new boolean[]{});
                            }
                        }
                    });
                    if (index == jobComponents.size() - 1) {
                        down.setEnabled(false);
                    }
                    down.setIcon(new ThemeResource("../runo/icons/32/arrow-down.png"));
                    down.setStyleName(Reindeer.BUTTON_LINK);
                    buttons.addComponent(up);
                    buttons.addComponent(down);
                    return buttons;
                }
            });
            setColumnWidth("Order", 65);

            addGeneratedColumn("Delete", new Table.ColumnGenerator() {

                @Override
                public Component generateCell(Table source, final Object itemId, Object columnId) {
                    Button delete = new Button("", new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            components.removeItem(itemId);
                            jobComponents.remove((T) itemId);
                        }
                    });
                    delete.setStyleName(Reindeer.BUTTON_LINK);
                    delete.setIcon(new ThemeResource("../runo/icons/32/cancel.png"));
                    return delete;
                }
            });
            setColumnWidth("Delete", 40);

        }
    }
}
