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
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import se.trixon.almond.util.fx.control.editable_list.EditableListItem;

/**
 *
 * @author Patrik Karlström
 */
public class Task implements EditableListItem {

//    private transient final ResourceBundle mBundle = NbBundle.getBundle(Task.class);
    @SerializedName("description")
    private String mDescription;
    @SerializedName("uuid")
    private String mId = UUID.randomUUID().toString();
    @SerializedName("last_run")
    private long mLastRun;
    @SerializedName("name")
    private String mName;
    @SerializedName("source")
    private File mSourceDir;

    public Task() {
    }

    public String getDescription() {
        return StringUtils.defaultString(mDescription);
    }

    public String getId() {
        return mId;
    }

    public long getLastRun() {
        return mLastRun;
    }

    @Override
    public String getName() {
        return mName;
    }

    public File getSourceDir() {
        return mSourceDir;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setLastRun(long lastRun) {
        mLastRun = lastRun;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setSourceDir(File sourceDir) {
        this.mSourceDir = sourceDir;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder("[INFO] %s".formatted(mName)).append("\n");
        sb.append(ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE));
        sb.append("\n");
        return sb.toString();
    }

    public ArrayList<String> getCommand() {
        return new ArrayList<>(List.of(mSourceDir.toString()));
//        return new ArrayList<>(List.of(mSourceDir.getAbsolutePath()));
//        return new ArrayList<>(List.of("kwrite"));
//        return new ArrayList<>(List.of("ls"));
//        return new ArrayList<>(List.of("ls", "TODO", "COMPUTE", "COMMAND"));
    }

}
