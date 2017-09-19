package pub.devrel.easypermissions.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

/**
 * Delegate class to make permission calls based on the 'host' (Fragment, Activity, etc).
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class PermissionHelper<T> {

    private static final String TAG = "PermissionHelper";

    private T mHost;

    @NonNull
    public static PermissionHelper newInstance(Activity host) {
        // 小于android 6.0的情况
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new LowApiPermissionsHelper(host);
        }
        //
        if (host instanceof AppCompatActivity) {
            return new AppCompatActivityPermissionHelper((AppCompatActivity) host);
        } else {
            return new ActivityPermissionHelper(host);
        }
    }

    @NonNull
    public static PermissionHelper newInstance(Fragment host) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new LowApiPermissionsHelper(host);
        }

        return new SupportFragmentPermissionHelper(host);
    }

    @NonNull
    public static PermissionHelper newInstance(android.app.Fragment host) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new LowApiPermissionsHelper(host);
        }

        return new FrameworkFragmentPermissionHelper(host);
    }

    // ============================================================================
    // Public concrete methods
    // ============================================================================

    public PermissionHelper(@NonNull T host) {
        mHost = host;
    }

    /**
     * 是否需要给出请求权限的原因
     *
     * @param perms
     * @return
     */
    public boolean shouldShowRationale(@NonNull String... perms) {
        for (String perm : perms) {
            if (shouldShowRequestPermissionRationale(perm)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 请求权限
     *
     * @param rationale
     * @param positiveButton
     * @param negativeButton
     * @param requestCode
     * @param perms
     */
    public void requestPermissions(@NonNull String rationale,
                                   @StringRes int positiveButton,
                                   @StringRes int negativeButton,
                                   int requestCode,
                                   @NonNull String... perms) {

        // 是否需要给出请求权限的原因
        if (shouldShowRationale(perms)) {
            //
            showRequestPermissionRationale(
                    rationale, positiveButton, negativeButton, requestCode, perms);
        } else {
            directRequestPermissions(requestCode, perms);
        }
    }

    /**
     * 权限是否被永久拒绝
     *
     * @param perms
     * @return
     */
    public boolean somePermissionPermanentlyDenied(@NonNull List<String> perms) {
        for (String deniedPermission : perms) {
            if (permissionPermanentlyDenied(deniedPermission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 权限是否被永久拒绝
     *
     * @param perms
     * @return
     */
    public boolean permissionPermanentlyDenied(@NonNull String perms) {
        return !shouldShowRequestPermissionRationale(perms);
    }

    public boolean somePermissionDenied(@NonNull String... perms) {
        return shouldShowRationale(perms);
    }

    @NonNull
    public T getHost() {
        return mHost;
    }

    // ============================================================================
    // Public abstract methods
    // ============================================================================

    /**
     * 请求对应权限
     *
     * @param requestCode
     * @param perms
     */
    public abstract void directRequestPermissions(int requestCode, @NonNull String... perms);

    public abstract boolean shouldShowRequestPermissionRationale(@NonNull String perm);

    public abstract void showRequestPermissionRationale(@NonNull String rationale,
                                                        @StringRes int positiveButton,
                                                        @StringRes int negativeButton,
                                                        int requestCode,
                                                        @NonNull String... perms);

    public abstract Context getContext();

}
