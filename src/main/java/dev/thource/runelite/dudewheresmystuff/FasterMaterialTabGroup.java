/*
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dev.thource.runelite.dudewheresmystuff;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * This class will be a container (group) for the new Material Tabs. It will contain a list of tabs
 * and a display (JPanel). When a tab is selected, the JPanel "display" will display the content
 * associated with that tab.
 *
 * <p>How to use these tabs:
 *
 * <ol>
 *   <li>1 - Create displays (JPanels) for each tab
 *   <li>2 - Create an empty JPanel to serve as the group's display
 *   <li>3 - Create a new MaterialGroup, passing the panel in step 2 as a param
 *   <li>4 - Create new tabs, passing the group in step 3 and one of the panels in step 1 as params
 *   <li>5 - Add the tabs to the group using the MaterialTabGroup#addTab method
 *   <li>6 - Select one of the tabs using the MaterialTab#select method
 * </ol>
 *
 * @author Psikoi
 */
public class FasterMaterialTabGroup extends JPanel {

  /* The panel on which the content tab's content will be displayed on. */
  private final JPanel display;
  /* A list of all the tabs contained in this group. */
  private final List<FasterMaterialTab> tabs = new ArrayList<>();
  private final List<FasterMaterialTab> endTabs = new ArrayList<>();
  private final transient DudeWheresMyStuffPlugin plugin;
  private final GridBagLayout gridBagLayout;

  FasterMaterialTabGroup(JPanel display, DudeWheresMyStuffPlugin plugin) {
    this.display = display;
    this.plugin = plugin;
    if (display != null) {
      this.display.setLayout(new BorderLayout());
    }
    this.gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    setOpaque(false);
  }

  public void addTab(FasterMaterialTab tab) {
    tabs.add(tab);
    add(tab);
  }

  public void addTabToEnd(FasterMaterialTab tab) {
    endTabs.add(tab);
    this.addTab(tab);
  }

  public void resetGrid() {
    GridBagConstraints constraints = new GridBagConstraints();
    // reduce padding if there are 6 tabs (f2p) so that kofi is distinguished from the other tabs
    int padding = tabs.stream().filter(Component::isVisible).count() == 6 ? 6 : 8;

    int visibleTabs = 0;
    for (FasterMaterialTab tab : tabs) {
      if (!tab.isVisible()) {
        continue;
      }

      constraints.anchor =
          endTabs.contains(tab) ? GridBagConstraints.EAST : GridBagConstraints.WEST;
      constraints.gridx = endTabs.contains(tab) ? 5 : visibleTabs % 6;
      constraints.gridy = visibleTabs / 6;
      constraints.weightx = endTabs.contains(tab) ? 1 : 0.1;
      constraints.insets = new Insets(constraints.gridy == 0 ? 0 : padding,
          constraints.gridx == 0 ? 0 : padding, 0, 0);
      gridBagLayout.setConstraints(tab, constraints);

      visibleTabs++;
    }
  }

  /**
   * Selects a tab from the group, and sets the display's content to the tab's associated content.
   *
   * @param selectedTab - The tab to select
   */
  public void select(FasterMaterialTab selectedTab) {
    if (!tabs.contains(selectedTab)) {
      return;
    }

    // If the OnTabSelected returned false, exit the method to prevent tab switching
    if (!selectedTab.select()) {
      return;
    }

    // If the display is available, switch from the old to the new display
    if (display != null) {
      EnhancedSwingUtilities.fastRemoveAll(display, plugin.getChatMessageManager());
      display.add(selectedTab.getContent());
      display.revalidate();
      display.repaint();
    }

    // Unselected all other tabs
    for (FasterMaterialTab tab : tabs) {
      if (!tab.equals(selectedTab)) {
        tab.unselect();
      }
    }
  }
}
