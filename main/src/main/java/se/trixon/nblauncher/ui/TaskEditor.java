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

import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javax.swing.JFileChooser;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.openide.DialogDescriptor;
import se.trixon.almond.nbp.Almond;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.control.FileChooserPaneSwingFx;
import se.trixon.nblauncher.core.StorageManager;
import se.trixon.nblauncher.core.Task;
import se.trixon.nblauncher.core.TaskManager;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class TaskEditor extends GridPane {

    private DialogDescriptor mDialogDescriptor;
    private TextField mNameTextField;
    private Task mTask;
    private final TaskManager mTaskManager = TaskManager.getInstance();
    private FileChooserPaneSwingFx mSourceChooserPane;

    public TaskEditor() {
        createUI();

//        initListeners();
        Platform.runLater(() -> {
            initValidation();
            mNameTextField.requestFocus();
        });
    }

    public void load(Task task, DialogDescriptor dialogDescriptor) {
        if (task == null) {
            task = new Task();
        }
        mDialogDescriptor = dialogDescriptor;
        mTask = task;
        mNameTextField.setText(task.getName());
        mSourceChooserPane.setPath(task.getSourceDir());
    }

    public Task save() {
        mTaskManager.getIdToItem().put(mTask.getId(), mTask);

        mTask.setName(mNameTextField.getText().trim());
        mTask.setSourceDir(mSourceChooserPane.getPath());

        StorageManager.save();

        return mTask;
    }

    private void createUI() {
        var nameLabel = new Label(Dict.NAME.toString());
        mNameTextField = new TextField();
        mSourceChooserPane = new FileChooserPaneSwingFx(Dict.OPEN.toString(), Dict.SOURCE.toString(), Almond.getFrame(), JFileChooser.FILES_ONLY);
        int col = 0;
        int row = 0;
        nameLabel.setPrefWidth(9999);
        add(nameLabel, col, row, REMAINING, 1);
        add(mNameTextField, col, ++row, REMAINING, 1);
        add(mSourceChooserPane, col, ++row, REMAINING, 1);

        var rowInsets = FxHelper.getUIScaledInsets(0, 0, 8, 0);

        GridPane.setMargin(mNameTextField, rowInsets);
        GridPane.setMargin(mSourceChooserPane, rowInsets);
    }

    private void initValidation() {
        var textRequired = "Text is required";
        var textUnique = "Text has to be unique";
        boolean indicateRequired = false;

        var namePredicate = (Predicate<String>) s -> {
            return mTaskManager.isValid(mTask.getName(), s);
        };

        var uniqueNamePredicate = (Predicate<String>) s -> {
            var newName = mNameTextField.getText();
            if (!mTaskManager.exists(newName)) {
                return true;
            } else {
                return StringUtils.equalsIgnoreCase(newName, mTask.getName());
            }
        };

        var validationSupport = new ValidationSupport();
        validationSupport.registerValidator(mNameTextField, indicateRequired, Validator.combine(
                Validator.createEmptyValidator(textRequired),
                Validator.createPredicateValidator(namePredicate, textUnique),
                Validator.createPredicateValidator(uniqueNamePredicate, textUnique)
        ));

        //validationSupport.registerValidator(mDescTextField, indicateRequired, Validator.createEmptyValidator(textRequired));
        validationSupport.registerValidator(mSourceChooserPane.getTextField(), indicateRequired, Validator.createEmptyValidator(textRequired));
//        validationSupport.registerValidator(mDestChooserPane.getTextField(), indicateRequired, Validator.createEmptyValidator(textRequired));
//        validationSupport.registerValidator(mFilePatternComboBox, indicateRequired, Validator.createEmptyValidator(textRequired));
//        validationSupport.registerValidator(mDatePatternComboBox, indicateRequired, Validator.createEmptyValidator(textRequired));
//        validationSupport.registerValidator(mDatePatternComboBox, indicateRequired, Validator.createPredicateValidator(datePredicate, textRequired));
        validationSupport.validationResultProperty().addListener((p, o, n) -> {
            mDialogDescriptor.setValid(!validationSupport.isInvalid());
        });

//        mFilePatternComboBox.getEditor().textProperty().addListener((p, o, n) -> {
//            mFilePatternComboBox.setValue(n);
//        });
//
//        mDatePatternComboBox.getEditor().textProperty().addListener((p, o, n) -> {
//            mDatePatternComboBox.setValue(n);
//        });
        validationSupport.initInitialDecoration();
    }

}
