package pub.devrel.easypermissions.helper;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Permissions helper for apps built against API < 23, which do not need runtime permissions.
 * <p>
 * 小于android 6.0的情况
 */
class LowApiPermissionsHelper extends PermissionHelper<Object> {

    public LowApiPermissionsHelper(@NonNull Object host) {
        super(host);
    }

    /**
     * 6.9以下，不需要请求权限
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        throw new IllegalStateException("Should never be requesting permissions on API < 23!");
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        return false;
    }

    @Override
    public void showRequestPermissionRationale(@NonNull String rationale,
                                               int positiveButton,
                                               int negativeButton,
                                               int requestCode,
                                               @NonNull String... perms) {
        throw new IllegalStateException("Should never be requesting permissions on API < 23!");
    }

    @Override
    public Context getContext() {
        return null;
    }
}
