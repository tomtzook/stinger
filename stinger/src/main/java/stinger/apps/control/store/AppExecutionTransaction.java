package stinger.apps.control.store;

import com.stinger.framework.db.Connection;
import com.stinger.framework.db.Transaction;
import stinger.apps.ExecutionState;
import stinger.apps.InstallationState;
import stinger.apps.StingerAppControl;

public class AppExecutionTransaction extends AppTransaction {

    private final StoredAppModel mModel;
    private final StingerAppControl mControl;

    public AppExecutionTransaction(Connection connection, Transaction transaction,
                                   StoredAppModel model, StingerAppControl control) {
        super(connection, transaction);
        mModel = model;
        mControl = control;
    }

    public void install() {
        if (mModel.getInstallationState() == InstallationState.INSTALLED) {
            return;
        }

        mControl.install();
        mModel.setInstallationState(InstallationState.INSTALLED);
    }

    public void start() {
        if (mModel.getExecutionState() == ExecutionState.RUNNING) {
            return;
        }

        install();

        mControl.start();
        mModel.setExecutionState(ExecutionState.RUNNING);
    }

    public void stop() {
        if (mModel.getExecutionState() == ExecutionState.NOT_RUNNING) {
            return;
        }

        mControl.stop();
        mModel.setExecutionState(ExecutionState.NOT_RUNNING);
    }

    public void uninstall() {
        stop();

        mControl.uninstall();
        mModel.setInstallationState(InstallationState.UNINSTALL_HAS_ENVIRONMENT);
    }
}
