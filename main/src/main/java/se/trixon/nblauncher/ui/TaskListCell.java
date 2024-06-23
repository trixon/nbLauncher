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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.control.editable_list.EditableListCell;
import se.trixon.nblauncher.core.ExecutorManager;
import se.trixon.nblauncher.core.Task;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class TaskListCell extends EditableListCell<Task> {

    private final Label mDescLabel = new Label();
    private final TaskListEditor mEditor;
    private final Label mNameLabel = new Label();
    private final Label mLastRunLabel = new Label();

    private final VBox mRoot = new VBox();

    public TaskListCell(TaskListEditor editor) {
        mEditor = editor;
        createUI();
    }

    @Override
    protected void updateItem(Task item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            clearContent();
        } else {
            addContent(item);
        }
    }

    private void addContent(Task task) {
        setText(null);
        mNameLabel.setText(task.getName());
//        mDescLabel.setText(task.getDescription());
        String lastRun = "-";
        if (task.getLastRun() != 0) {
            var ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(task.getLastRun()), ZoneId.systemDefault());
            lastRun = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss"));
        }
        mLastRunLabel.setText(lastRun);

        mRoot.getChildren().setAll(mNameLabel, mDescLabel, mLastRunLabel);
        mRoot.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                if (mouseEvent.isControlDown()) {
                    mEditor.editTask(null, task);
                } else {
                    ExecutorManager.getInstance().requestStart(task);
                }
            }
        });
        setGraphic(mRoot);
    }

    private void clearContent() {
        setText(null);
        setGraphic(null);
    }

    private void createUI() {
        var fontSize = FxHelper.getScaledFontSize();
        var fontStyle = "-fx-font-size: %.0fpx; -fx-font-weight: %s;";

        mNameLabel.setStyle(fontStyle.formatted(fontSize * 1.4, "bold"));
        mDescLabel.setStyle(fontStyle.formatted(fontSize * 1.1, "normal"));
        mLastRunLabel.setStyle(fontStyle.formatted(fontSize * 1.1, "normal"));
    }

}
