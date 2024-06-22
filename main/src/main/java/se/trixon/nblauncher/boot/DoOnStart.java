/* 
 * Copyright 2024 Patrik Karlström <patrik@trixon.se>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.trixon.nblauncher.boot;

import java.io.IOException;
import javax.swing.SwingUtilities;
import org.openide.modules.OnStart;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import se.trixon.almond.nbp.NbHelper;
import se.trixon.almond.nbp.dialogs.NbOptionalDialog;
import se.trixon.nblauncher.core.StorageManager;

/**
 *
 * @author Patrik Karlström
 */
@OnStart
public class DoOnStart implements Runnable {

    static {
//        UIManager.put("EditorTabDisplayerUI", NoTabsTabDisplayerUI.class.getName());
        System.setProperty("netbeans.winsys.no_help_in_dialogs", "true");
        System.setProperty("netbeans.winsys.no_toolbars", "true");
        System.setProperty("netbeans.winsys.status_line.path", "");

        NbHelper.setLafDefault("Light");
        NbHelper.setLafAccentColor("#00ff00");
        NbHelper.initNightModeIfNeeded();

        NbOptionalDialog.setPreferences(NbPreferences.forModule(NbOptionalDialog.class).node("optionalDialogState"));
    }

    @Override
    public void run() {
        try {
            StorageManager.getInstance().load();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        SwingUtilities.invokeLater(() -> {
//            UIManager.put("ViewTabDisplayerUI", NoTabsTabDisplayerUI.class.getName());
//            UIManager.put("EditorTabDisplayerUI", NoTabsTabDisplayerUI.class.getName());
//            UIManager.put("NbMainWindow.showCustomBackground", Boolean.TRUE);
        });
    }

}
