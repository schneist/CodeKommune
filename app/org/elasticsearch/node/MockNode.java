package org.elasticsearch.node;

import org.elasticsearch.Version;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugins.Plugin;

import java.util.ArrayList;
import java.util.Collection;

public class MockNode extends Node {

  public MockNode(Settings settings,Version version, Collection<Class<? extends Plugin>> classpathPlugins) {
    super(InternalSettingsPreparer.prepareEnvironment(settings, null), version, classpathPlugins);
  }

  private static Collection<Class<? extends Plugin>> list(Class<? extends Plugin> classpathPlugin) {
    Collection<Class<? extends Plugin>> list = new ArrayList<>();
    list.add(classpathPlugin);
    return list;
  }

}