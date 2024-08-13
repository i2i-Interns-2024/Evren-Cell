package com.sk.i2i.smsapp.ui;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

@Plugin(name = "CustomLogAppender", category = "Core", elementType = Appender.ELEMENT_TYPE)
public class CustomLogAppender extends AbstractAppender {

    private static LogWindow logWindow;

    protected CustomLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
        logWindow = new LogWindow();
    }

    @Override
    public void append(LogEvent event) {
        logWindow.appendLog(new String(getLayout().toByteArray(event)));
    }

    @PluginFactory
    public static CustomLogAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter) {

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new CustomLogAppender(name, filter, layout, true);
    }
}
