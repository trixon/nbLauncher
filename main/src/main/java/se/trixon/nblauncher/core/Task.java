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
package se.trixon.nblauncher.core;

import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.NbBundle;
import se.trixon.almond.util.fx.control.editable_list.EditableListItem;

/**
 *
 * @author Patrik Karlström
 */
public class Task implements EditableListItem {

    private transient final ResourceBundle mBundle = NbBundle.getBundle(Task.class);

    @SerializedName("cacheDir")
    private File mCacheDir;
    @SerializedName("cacheDirActivated")
    private boolean mCacheDirActivated;
    @SerializedName("consoleLogger")
    private boolean mConsoleLogger;
    @SerializedName("environment")
    private String mEnvironment;
    @SerializedName("execPath")
    private File mExecPath;
    @SerializedName("fontSize")
    private String mFontSize;
    @SerializedName("uuid")
    private String mId = UUID.randomUUID().toString();
    @SerializedName("javaDir")
    private File mJavaDir;
    @SerializedName("javaDirActivated")
    private boolean mJavaDirActivated;
    @SerializedName("last_run")
    private long mLastRun;
    @SerializedName("locale")
    private String mLocale;
    @SerializedName("name")
    private String mName;
    @SerializedName("userDir")
    private File mUserDir;
    @SerializedName("userDirActivated")
    private boolean mUserDirActivated;

    public Task() {
    }

    public File getCacheDir() {
        return mCacheDir;
    }

    public ArrayList<String> getCommand() {
        var cmd = new ArrayList<String>();
        cmd.add(mExecPath.toString());

        addOptional(cmd, true, "--fontsize", mFontSize);
        addOptional(cmd, true, "--locale", mLocale);
        addOptional(cmd, mUserDirActivated, "--userdir", mUserDir);
        addOptional(cmd, mCacheDirActivated, "--cachedir", mCacheDir);
        addOptional(cmd, mJavaDirActivated, "--jdkhome", mJavaDir);

        addOptionalEnvironment(cmd, true, "netbeans.logger.console=" + (mConsoleLogger ? "true" : "false"));

        if (StringUtils.isNotBlank(mEnvironment)) {
            Arrays.stream(StringUtils.split(mEnvironment, "\n"))
                    .filter(s -> !StringUtils.startsWith(s, "#"))
                    .filter(s -> StringUtils.contains(s, "="))
                    .forEachOrdered(s -> {
                        addOptionalEnvironment(cmd, true, s);
                    });
        }

        return cmd;
    }

    public String getCommandAsString() {
        return String.join(" ", getCommand());
    }

    public String getEnvironment() {
        return mEnvironment;
    }

    public File getExecPath() {
        return mExecPath;
    }

    public String getFontSize() {
        return mFontSize;
    }

    public String getId() {
        return mId;
    }

    public File getJavaDir() {
        return mJavaDir;
    }

    public long getLastRun() {
        return mLastRun;
    }

    public Locale getLocale() {
        return Locale.forLanguageTag(StringUtils.defaultIfBlank(mLocale, "und"));
    }

    @Override
    public String getName() {
        return mName;
    }

    public File getUserDir() {
        return mUserDir;
    }

    public boolean isCacheDirActivated() {
        return mCacheDirActivated;
    }

    public boolean isConsoleLogger() {
        return mConsoleLogger;
    }

    public boolean isJavaDirActivated() {
        return mJavaDirActivated;
    }

    public boolean isUserDirActivated() {
        return mUserDirActivated;
    }

    public void setCacheDir(File cacheDir) {
        this.mCacheDir = cacheDir;
    }

    public void setCacheDirActivated(boolean cacheDirActivated) {
        this.mCacheDirActivated = cacheDirActivated;
    }

    public void setConsoleLogger(boolean consoleLogger) {
        this.mConsoleLogger = consoleLogger;
    }

    public void setEnvironment(String environment) {
        this.mEnvironment = environment;
    }

    public void setExecPath(File execPath) {
        this.mExecPath = execPath;
    }

    public void setFontSize(String fontSize) {
        this.mFontSize = fontSize;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setJavaDir(File javaDir) {
        this.mJavaDir = javaDir;
    }

    public void setJavaDirActivated(boolean javaDirActivated) {
        this.mJavaDirActivated = javaDirActivated;
    }

    public void setLastRun(long lastRun) {
        mLastRun = lastRun;
    }

    public void setLocale(Locale locale) {
        if (StringUtils.equalsIgnoreCase(locale.toLanguageTag(), "und")) {
            mLocale = "";
        } else {
            mLocale = locale.toLanguageTag();
        }
    }

    public void setName(String name) {
        mName = name;
    }

    public void setUserDir(File userDir) {
        this.mUserDir = userDir;
    }

    public void setUserDirActivated(boolean userDirActivated) {
        this.mUserDirActivated = userDirActivated;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder("[INFO] %s".formatted(mName)).append("\n");
        sb.append(getCommandAsString());
        sb.append("\n");
        return sb.toString();
    }

    private void addOptional(ArrayList<String> cmd, boolean condition, String key, Object value) {
        if (value != null && condition) {
            var val = "";
            switch (value) {
                case String s -> {
                    val = s;
                }
                case File f -> {
                    val = f.getAbsolutePath();
                }
                default -> {
                }
            }
            if (StringUtils.isNotBlank(val)) {
                cmd.add(key);
                cmd.add(val);
            }
        }
    }

    private void addOptionalEnvironment(ArrayList<String> cmd, boolean conditional, String keyVal) {
        if (conditional) {
            cmd.add("-J-D" + keyVal);
        }
    }

}
