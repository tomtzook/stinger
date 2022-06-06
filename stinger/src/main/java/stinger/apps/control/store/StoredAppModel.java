package stinger.apps.control.store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import stinger.apps.AppEnvironment;
import stinger.apps.ExecutionState;
import stinger.apps.ExecutionType;
import stinger.apps.InstallationState;
import stinger.apps.control.DefaultAppEnvironment;
import stinger.util.FileHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Entity
@Table(name = "app")
public class StoredAppModel {

    @Id
    @Column(name = "appId", nullable = false)
    private Integer mAppId;
    @Column(name = "version", nullable = false)
    private String mVersion;
    @Column(name = "appPath", nullable = true)
    private String mAppPath;
    @Enumerated(EnumType.STRING)
    @Column(name = "exec_type", nullable = false)
    private ExecutionType mExecutionType;
    @Enumerated(EnumType.STRING)
    @Column(name = "install_state", nullable = false)
    private InstallationState mInstallationState;
    @Enumerated(EnumType.STRING)
    @Column(name = "exec_state", nullable = false)
    private ExecutionState mExecutionState;

    public Integer getAppId() {
        return mAppId;
    }

    public void setAppId(Integer appId) {
        mAppId = appId;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getAppPath() {
        return mAppPath;
    }

    public void setAppPath(String appPath) {
        mAppPath = appPath;
    }

    public ExecutionType getExecutionType() {
        return mExecutionType;
    }

    public void setExecutionType(ExecutionType executionType) {
        mExecutionType = executionType;
    }

    public InstallationState getInstallationState() {
        return mInstallationState;
    }

    public void setInstallationState(InstallationState installationState) {
        mInstallationState = installationState;
    }

    public ExecutionState getExecutionState() {
        return mExecutionState;
    }

    public void setExecutionState(ExecutionState executionState) {
        mExecutionState = executionState;
    }

    public AppEnvironment getEnvironment() throws IOException {
        return getEnvironment(false);
    }

    public AppEnvironment getEnvironment(boolean clear) throws IOException {
        Path appRoot = Paths.get(mAppPath);
        if (clear && Files.exists(appRoot)) {
            FileHelper.recursiveDelete(appRoot);
        }

        return DefaultAppEnvironment.create(appRoot);
    }
}
