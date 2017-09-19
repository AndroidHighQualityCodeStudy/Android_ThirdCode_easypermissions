package pub.devrel.easypermissions.helper;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import pub.devrel.easypermissions.RationaleDialogFragmentCompat;

/**
 * Implementation of {@link PermissionHelper} for Support Library host classes.
 */
public abstract class BaseSupportPermissionsHelper<T> extends PermissionHelper<T> {

    public BaseSupportPermissionsHelper(@NonNull T host) {
        super(host);
    }

    public abstract FragmentManager getSupportFragmentManager();

    /**
     * 申请权限 原因 弹窗
     *
     * @param rationale
     * @param positiveButton
     * @param negativeButton
     * @param requestCode
     * @param perms
     */
    @Override
    public void showRequestPermissionRationale(@NonNull String rationale,
                                               int positiveButton,
                                               int negativeButton,
                                               int requestCode,
                                               @NonNull String... perms) {
        // 申请 权限 原因的弹窗
        RationaleDialogFragmentCompat
                .newInstance(positiveButton, negativeButton, rationale, requestCode, perms)
                .show(getSupportFragmentManager(), RationaleDialogFragmentCompat.TAG);
    }
}
