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

import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javax.swing.JFileChooser;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.openide.DialogDescriptor;
import org.openide.NotificationLineSupport;
import org.openide.util.NbBundle;
import se.trixon.almond.nbp.Almond;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.control.FileChooserPaneSwingFx;
import se.trixon.almond.util.fx.control.LocaleComboBox;
import se.trixon.nblauncher.core.StorageManager;
import se.trixon.nblauncher.core.Task;
import se.trixon.nblauncher.core.TaskManager;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class TaskEditor extends GridPane {

    private final TextArea mArgTextArea = new TextArea();
    private FileChooserPaneSwingFx mCacheDirChooserPane;
    private DialogDescriptor mDialogDescriptor;
    private final TextArea mEnvTextArea = new TextArea();
    private FileChooserPaneSwingFx mExecPathChooserPane;
    private final ComboBox<String> mFontSizeComboBox = new ComboBox<>();
    private FileChooserPaneSwingFx mJavaDirChooserPane;
    private final LocaleComboBox mLocaleComboBox = new LocaleComboBox();
    private final CheckBox mLoggerCheckBox = new CheckBox("Log to console");
    private TextField mNameTextField;
    private NotificationLineSupport mNotificationLineSupport;
    private Task mTask;
    private final TaskManager mTaskManager = TaskManager.getInstance();
    private FileChooserPaneSwingFx mUserDirChooserPane;
    private final ResourceBundle mBundle = NbBundle.getBundle(TaskEditor.class);

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
        mLocaleComboBox.setValue(task.getLocale());
        mFontSizeComboBox.setValue(task.getFontSize());
        mLoggerCheckBox.setSelected(task.isConsoleLogger());

        mExecPathChooserPane.setPath(task.getExecPath());
        mJavaDirChooserPane.setPath(task.getJavaDir());

        mUserDirChooserPane.setPath(task.getUserDir());
        mCacheDirChooserPane.setPath(task.getCacheDir());

        mJavaDirChooserPane.getCheckBox().setSelected(task.isJavaDirActivated());
        mUserDirChooserPane.getCheckBox().setSelected(task.isUserDirActivated());
        mCacheDirChooserPane.getCheckBox().setSelected(task.isCacheDirActivated());

        mArgTextArea.setText(task.getArg());
        mEnvTextArea.setText(task.getEnv());
    }

    public Task save() {
        mTaskManager.getIdToItem().put(mTask.getId(), mTask);

        mTask.setName(mNameTextField.getText().trim());
        mTask.setLocale(mLocaleComboBox.getValue());
        mTask.setFontSize(mFontSizeComboBox.getValue());
        mTask.setConsoleLogger(mLoggerCheckBox.isSelected());

        mTask.setExecPath(mExecPathChooserPane.getPath());
        mTask.setJavaDir(mJavaDirChooserPane.getPath());

        mTask.setUserDir(mUserDirChooserPane.getPath());
        mTask.setCacheDir(mCacheDirChooserPane.getPath());

        mTask.setJavaDirActivated(mJavaDirChooserPane.getCheckBox().isSelected());
        mTask.setUserDirActivated(mUserDirChooserPane.getCheckBox().isSelected());
        mTask.setCacheDirActivated(mCacheDirChooserPane.getCheckBox().isSelected());

        mTask.setArg(mArgTextArea.getText());
        mTask.setEnv(mEnvTextArea.getText());

        StorageManager.save();

        return mTask;
    }

    void setNotificationLineSupport(NotificationLineSupport notificationLineSupport) {
        mNotificationLineSupport = notificationLineSupport;
    }

    private void createUI() {
        var nameLabel = new Label(Dict.NAME.toString());
        var localeLabel = new Label("Locale");
        var fontSizeLabel = new Label("Font size");
        var argLabel = new Label("Additional arguments");
        var envLabel = new Label("Environment");
        mNameTextField = new TextField();
        mExecPathChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), "Executable", Almond.getFrame(), JFileChooser.FILES_ONLY);
        mUserDirChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), Almond.getFrame(), JFileChooser.DIRECTORIES_ONLY, "User directory");
        mCacheDirChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), Almond.getFrame(), JFileChooser.DIRECTORIES_ONLY, "Cache directory");
        mJavaDirChooserPane = new FileChooserPaneSwingFx(Dict.SELECT.toString(), Almond.getFrame(), JFileChooser.DIRECTORIES_ONLY, "Java");
        mArgTextArea.setPromptText(mBundle.getString("hintArg"));
        mEnvTextArea.setPromptText(mBundle.getString("hintEnv"));

        int col = 0;
        int row = 0;
        nameLabel.setPrefWidth(9999);
        mFontSizeComboBox.setPrefWidth(9999);
        addRow(++row, nameLabel, new Region(), localeLabel, fontSizeLabel);
        addRow(++row, mNameTextField, mLoggerCheckBox, mLocaleComboBox, mFontSizeComboBox);
        add(mExecPathChooserPane, col, ++row, 2, 1);
        add(mJavaDirChooserPane, 2, row, 2, 1);
        add(mUserDirChooserPane, col, ++row, 2, 1);
        add(mCacheDirChooserPane, 2, row, 2, 1);
        add(argLabel, col, ++row, 2, 1);
        add(envLabel, 2, row, 2, 1);
        add(mArgTextArea, 0, ++row, 2, REMAINING);
        add(mEnvTextArea, 2, row, 2, REMAINING);

        var rowInsets = FxHelper.getUIScaledInsets(0, 0, 8, 0);

        GridPane.setMargin(mNameTextField, rowInsets);
        GridPane.setMargin(mLocaleComboBox, rowInsets);
        GridPane.setMargin(mFontSizeComboBox, rowInsets);
        GridPane.setMargin(mExecPathChooserPane, rowInsets);
        GridPane.setMargin(mUserDirChooserPane, rowInsets);
        GridPane.setMargin(mCacheDirChooserPane, rowInsets);
        GridPane.setMargin(mJavaDirChooserPane, rowInsets);
        GridPane.setMargin(mLoggerCheckBox, rowInsets);
        setHgap(FxHelper.getUIScaled(12.0));
        FxHelper.autoSizeColumn(this, 4);
        mArgTextArea.setPrefHeight(9999);
        mEnvTextArea.setPrefHeight(9999);
        mFontSizeComboBox.getItems().setAll("",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "16",
                "18",
                "20",
                "21",
                "22",
                "24",
                "26",
                "28",
                "32",
                "36",
                "40",
                "42",
                "44",
                "48",
                "54",
                "60",
                "66",
                "72"
        );
    }

    private void initValidation() {
        var textRequired = "Text is required";
        var textUnique = "Text has to be unique";
        boolean indicateRequired = false;

        var userCachePredicate = (Predicate<String>) s -> {
            return !isInvalidUserChache();
        };
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

        validationSupport.registerValidator(mExecPathChooserPane.getTextField(), indicateRequired, Validator.createEmptyValidator(textRequired));
        validationSupport.registerValidator(mUserDirChooserPane.getTextField(), indicateRequired, Validator.createPredicateValidator(userCachePredicate, "clash"));
        validationSupport.registerValidator(mCacheDirChooserPane.getTextField(), indicateRequired, Validator.createPredicateValidator(userCachePredicate, "clash"));

        validationSupport.validationResultProperty().addListener((p, o, n) -> {
            mDialogDescriptor.setValid(!validationSupport.isInvalid() && !FxHelper.isFocusedNodeOfType(mNameTextField.getScene(), TextArea.class));
        });

        ChangeListener<Boolean> selectionListener = (p, o, n) -> {
            validationSupport.revalidate();

            if (isInvalidUserChache()) {
                mNotificationLineSupport.setErrorMessage("User directory must be different from cache directory.");
            } else {
                mNotificationLineSupport.clearMessages();
            }
        };

        mUserDirChooserPane.getCheckBox().selectedProperty().addListener(selectionListener);
        mCacheDirChooserPane.getCheckBox().selectedProperty().addListener(selectionListener);

        ChangeListener<Boolean> focusListener = (p, o, n) -> {
            validationSupport.revalidate();

            if (FxHelper.isFocusedNodeOfType(mNameTextField.getScene(), TextArea.class)) {
                mNotificationLineSupport.setInformationMessage("Exit text area in order to close the dialog");
            } else {
                mNotificationLineSupport.clearMessages();
            }
        };

        mArgTextArea.focusedProperty().addListener(focusListener);
        mEnvTextArea.focusedProperty().addListener(focusListener);

        validationSupport.initInitialDecoration();
    }

    private boolean isInvalidUserChache() {
        return mUserDirChooserPane.getCheckBox().isSelected()
                && mCacheDirChooserPane.getCheckBox().isSelected()
                && StringUtils.equalsIgnoreCase(mUserDirChooserPane.getPathAsString(), mCacheDirChooserPane.getPathAsString());
    }

}
