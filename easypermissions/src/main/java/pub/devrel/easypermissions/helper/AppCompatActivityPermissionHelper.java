package pub.devrel.easypermissions.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Permissions helper for {@link AppCompatActivity}.
 */
class AppCompatActivityPermissionHelper extends BaseSupportPermissionsHelper<AppCompatActivity> {

    public AppCompatActivityPermissionHelper(AppCompatActivity host) {
        super(host);
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        return getHost().getSupportFragmentManager();
    }

    /**
     * 请求权限
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        // 请求权限
        ActivityCompat.requestPermissions(getHost(), perms, requestCode);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        boolean flag = ActivityCompat.shouldShowRequestPermissionRationale(getHost(), perm);
        Log.e("xiaxl: ","---shouldShowRequestPermissionRationale---");
        Log.e("xiaxl: ","flag: "+flag);
        // 是否应该给出原因：为什么请求该perm权限
        return flag;
    }

    @Override
    public Context getContext() {
        return getHost();
    }
}
