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
package se.trixon.nblauncher.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import javafx.application.Platform;
import javafx.scene.Scene;
import javax.swing.SwingUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import se.trixon.almond.nbp.fx.FxDialogPanel;
import se.trixon.almond.nbp.fx.NbEditableList;
import se.trixon.almond.nbp.output.OutputHelper;
import se.trixon.almond.nbp.output.OutputLineMode;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.control.editable_list.EditableList;
import se.trixon.almond.util.swing.SwingHelper;
import se.trixon.nblauncher.core.ExecutorManager;
import se.trixon.nblauncher.core.StorageManager;
import static se.trixon.nblauncher.core.StorageManager.JSON;
import se.trixon.nblauncher.core.Task;
import se.trixon.nblauncher.core.TaskManager;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class TaskListEditor {

    private EditableList<Task> mEditableList;
    private final TaskManager mTaskManager = TaskManager.getInstance();

    public TaskListEditor() {
        init();
    }

    public EditableList<Task> getEditableList() {
        return mEditableList;
    }

    void editTask(String title, Task task) {
        var editor = new TaskEditor();
        editor.setPadding(FxHelper.getUIScaledInsets(16, 16, 0, 16));
        var dialogPanel = new FxDialogPanel() {
            @Override
            protected void fxConstructor() {
                setScene(new Scene(editor));
            }
        };
        dialogPanel.setPreferredSize(SwingHelper.getUIScaledDim(960, 360));

        SwingUtilities.invokeLater(() -> {
            editor.setPrefSize(FxHelper.getUIScaled(900), FxHelper.getUIScaled(660));
            var d = new DialogDescriptor(dialogPanel, Objects.toString(title, Dict.EDIT.toString()));
            d.setValid(false);
            editor.setNotificationLineSupport(d.createNotificationLineSupport());
            dialogPanel.setNotifyDescriptor(d);
            dialogPanel.initFx(() -> {
                editor.load(task, d);
            });

            if (DialogDescriptor.OK_OPTION == DialogDisplayer.getDefault().notify(d)) {
                Platform.runLater(() -> {
                    var editedItem = editor.save();
                    postEdit(mTaskManager.getById(editedItem.getId()));
                });
            }
        });
    }

    private void displayInfo(Task task) {
        var io = IOProvider.getDefault().getIO(Dict.INFORMATION.toString(), false);
        var outputHelper = new OutputHelper(Dict.INFORMATION.toString(), io, false);

        io.select();
        outputHelper.println(OutputLineMode.INFO, task.toString());

        StatusDisplayer.getDefault().setStatusText(String.join(" ", task.getCommand()));
    }

    private void init() {
        mEditableList = new NbEditableList.Builder<Task>()
                .setItemSingular(Dict.APPLICATION.toString())
                .setItemPlural(Dict.APPLICATIONS.toString())
                .setItemsProperty(mTaskManager.itemsProperty())
                .setOnEdit((title, task) -> {
                    editTask(title, task);
                })
                .setOnRemoveAll(() -> {
                    mTaskManager.getIdToItem().clear();
                    StorageManager.save();
                })
                .setOnRemove(t -> {
                    mTaskManager.getIdToItem().remove(t.getId());
                    StorageManager.save();
                })
                .setOnClone(t -> {
                    try {
                        var original = t;
                        var json = JSON.writeValueAsString(original);
                        var clone = JSON.readValue(json, original.getClass());
                        var uuid = UUID.randomUUID().toString();
                        clone.setId(uuid);
                        clone.setLastRun(0);
                        clone.setName("%s %s".formatted(clone.getName(), LocalDate.now().toString()));
                        mTaskManager.getIdToItem().put(clone.getId(), clone);

                        StorageManager.save();

                        return mTaskManager.getById(uuid);
                    } catch (JsonProcessingException ex) {
                        Exceptions.printStackTrace(ex);
                        return null;
                    }
                })
                .setOnStart(task -> ExecutorManager.getInstance().requestStart(task))
                .setOnSelect((t, u) -> {
                    if (u != null) {
                        displayInfo(u);
                    }
                })
                .build();

        mEditableList.getListView().setCellFactory(listView -> new TaskListCell(this));
    }

    private void postEdit(Task task) {
        mEditableList.postEdit(task);
    }

}
