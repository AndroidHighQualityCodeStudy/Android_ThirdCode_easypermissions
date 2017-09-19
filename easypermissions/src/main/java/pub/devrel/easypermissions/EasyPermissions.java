/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pub.devrel.easypermissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.helper.PermissionHelper;

/**
 * Utility to request and check System permissions for apps targeting Android M (API &gt;= 23).
 */
public class EasyPermissions {

    /**
     * Callback interface to receive the results of {@code EasyPermissions.requestPermissions()}
     * calls.
     */
    public interface PermissionCallbacks extends ActivityCompat.OnRequestPermissionsResultCallback {

        void onPermissionsGranted(int requestCode, List<String> perms);

        void onPermissionsDenied(int requestCode, List<String> perms);

    }

    private static final String TAG = "EasyPermissions";

    /**
     * 检测是否有权限缺失
     * Check if the calling context has a set of permissions.
     *
     * @param context the calling context.
     * @param perms   one ore more permissions, such as {@link Manifest.permission#CAMERA}.
     * @return true if all permissions are already granted, false if at least one permission is not
     * yet granted.
     * @see Manifest.permission
     */
    public static boolean hasPermissions(Context context, @NonNull String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        // 如果是6.0以下，都按有权限计算
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");
            // DANGER ZONE!!! Changing this will break the library.
            return true;
        }

        // Null context may be passed if we have detected Low API (less than M) so getting
        // to this point with a null context should not be possible.
        // context 空指针判断
        if (context == null) {
            throw new IllegalArgumentException("Can't check permissions for null context");
        }

        // 有一个权限被拒绝，则返回false
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 请求权限 Activity
     * <p>
     * Request permissions from an Activity with standard OK/Cancel buttons.
     *
     * @see #requestPermissions(Activity, String, int, int, int, String...)
     */
    public static void requestPermissions(@NonNull Activity host,
                                          @NonNull String rationale,
                                          int requestCode,
                                          @NonNull String... perms) {
        requestPermissions(host, rationale, android.R.string.ok, android.R.string.cancel,
                requestCode, perms);
    }

    /**
     * 请求权限 fragment
     * <p>
     * Request permissions from a Support Fragment with standard OK/Cancel buttons.
     *
     * @see #requestPermissions(Activity, String, int, int, int, String...)
     */
    public static void requestPermissions(
            @NonNull Fragment host,
            @NonNull String rationale,
            int requestCode,
            @NonNull String... perms) {

        requestPermissions(host, rationale, android.R.string.ok, android.R.string.cancel,
                requestCode, perms);
    }

    /**
     * 请求权限 android.app.Fragment
     * <p>
     * Request permissions from a standard Fragment with standard OK/Cancel buttons.
     *
     * @see #requestPermissions(Activity, String, int, int, int, String...)
     */
    public static void requestPermissions(
            @NonNull android.app.Fragment host,
            @NonNull String rationale,
            int requestCode,
            @NonNull String... perms) {

        requestPermissions(host, rationale, android.R.string.ok, android.R.string.cancel,
                requestCode, perms);
    }

    /**
     * 请求权限 Activity
     * <p>
     * Request a set of permissions, showing rationale if the system requests it.
     *
     * @param host           requesting context.
     * @param rationale      a message explaining why the application needs this set of permissions,
     *                       will be displayed if the user rejects the request the first time.
     * @param positiveButton custom text for positive button
     * @param negativeButton custom text for negative button
     * @param requestCode    request code to track this request, must be &lt; 256.
     * @param perms          a set of permissions to be requested.
     * @see Manifest.permission
     */
    public static void requestPermissions(
            @NonNull Activity host,
            @NonNull String rationale,
            @StringRes int positiveButton,
            @StringRes int negativeButton,
            int requestCode,
            @NonNull String... perms) {
        requestPermissions(PermissionHelper.newInstance(host), rationale,
                positiveButton, negativeButton,
                requestCode, perms);
    }

    /**
     * Request permissions from a Support Fragment.
     *
     * @see #requestPermissions(Activity, String, int, int, int, String...)
     */
    public static void requestPermissions(
            @NonNull Fragment host, @NonNull String rationale,
            @StringRes int positiveButton, @StringRes int negativeButton,
            int requestCode, @NonNull String... perms) {
        requestPermissions(PermissionHelper.newInstance(host), rationale,
                positiveButton, negativeButton,
                requestCode, perms);
    }

    /**
     * @see #requestPermissions(Activity, String, int, int, int, String...)
     */
    public static void requestPermissions(
            @NonNull android.app.Fragment host, @NonNull String rationale,
            @StringRes int positiveButton, @StringRes int negativeButton,
            int requestCode, @NonNull String... perms) {
        requestPermissions(PermissionHelper.newInstance(host), rationale,
                positiveButton, negativeButton,
                requestCode, perms);
    }

    /**
     * 请求权限
     *
     * @param helper
     * @param rationale
     * @param positiveButton
     * @param negativeButton
     * @param requestCode
     * @param perms
     */
    private static void requestPermissions(
            @NonNull PermissionHelper helper,
            @NonNull String rationale,
            @StringRes int positiveButton,
            @StringRes int negativeButton,
            int requestCode,
            @NonNull String... perms) {

        // 如果所有权限都存在
        // Check for permissions before dispatching the request
        if (hasPermissions(helper.getContext(), perms)) {
            notifyAlreadyHasPermissions(helper.getHost(), requestCode, perms);
            return;
        }
        // 有未赋予的权限，则请求权限
        // Request permissions
        helper.requestPermissions(rationale, positiveButton,
                negativeButton, requestCode, perms);
    }

    /**
     * 回调权限授予情况
     * <p>
     * Handle the result of a permission request, should be called from the calling {@link
     * Activity}'s {@link ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int,
     * String[], int[])} method.
     * <p>
     * If any permissions were granted or denied, the {@code object} will receive the appropriate
     * callbacks through {@link PermissionCallbacks} and methods annotated with {@link
     * AfterPermissionGranted} will be run if appropriate.
     *
     * @param requestCode  requestCode argument to permission result callback.
     * @param permissions  permissions argument to permission result callback.
     * @param grantResults grantResults argument to permission result callback.
     * @param receivers    an array of objects that have a method annotated with {@link
     *                     AfterPermissionGranted} or implement {@link PermissionCallbacks}.
     */
    public static void onRequestPermissionsResult(int requestCode,
                                                  @NonNull String[] permissions,
                                                  @NonNull int[] grantResults,
                                                  @NonNull Object... receivers) {

        // 分为两个数组，权限授予数组与权限未被授予数组
        // Make a collection of granted and denied permissions from the request.
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        // 回调权限被授予
        // iterate through all receivers
        for (Object object : receivers) {
            // Report granted permissions, if any.
            if (!granted.isEmpty()) {
                if (object instanceof PermissionCallbacks) {
                    ((PermissionCallbacks) object).onPermissionsGranted(requestCode, granted);
                }
            }
            // 回调权限被拒绝
            // Report denied permissions, if any.
            if (!denied.isEmpty()) {
                if (object instanceof PermissionCallbacks) {
                    ((PermissionCallbacks) object).onPermissionsDenied(requestCode, denied);
                }
            }
            // 如果权限被全部授予，则回调AfterPermissionGranted注解方法
            // If 100% successful, call annotated methods
            if (!granted.isEmpty() && denied.isEmpty()) {
                runAnnotatedMethods(object, requestCode);
            }
        }
    }

    /**
     * Check if at least one permission in the list of denied permissions has been permanently
     * denied (user clicked "Never ask again").
     *
     * @param host              context requesting permissions.
     * @param deniedPermissions list of denied permissions, usually from {@link
     *                          PermissionCallbacks#onPermissionsDenied(int, List)}
     * @return {@code true} if at least one permission in the list was permanently denied.
     */
    public static boolean somePermissionPermanentlyDenied(@NonNull Activity host,
                                                          @NonNull List<String> deniedPermissions) {
        return PermissionHelper.newInstance(host)
                .somePermissionPermanentlyDenied(deniedPermissions);
    }

    /**
     * @see #somePermissionPermanentlyDenied(Activity, List)
     */
    public static boolean somePermissionPermanentlyDenied(@NonNull Fragment host,
                                                          @NonNull List<String> deniedPermissions) {
        return PermissionHelper.newInstance(host)
                .somePermissionPermanentlyDenied(deniedPermissions);
    }

    /**
     * @see #somePermissionPermanentlyDenied(Activity, List).
     */
    public static boolean somePermissionPermanentlyDenied(@NonNull android.app.Fragment host,
                                                          @NonNull List<String> deniedPermissions) {
        return PermissionHelper.newInstance(host)
                .somePermissionPermanentlyDenied(deniedPermissions);
    }

    /**
     * Check if a permission has been permanently denied (user clicked "Never ask again").
     *
     * @param host             context requesting permissions.
     * @param deniedPermission denied permission.
     * @return {@code true} if the permissions has been permanently denied.
     */
    public static boolean permissionPermanentlyDenied(@NonNull Activity host,
                                                      @NonNull String deniedPermission) {
        return PermissionHelper.newInstance(host).permissionPermanentlyDenied(deniedPermission);
    }

    /**
     * @see #permissionPermanentlyDenied(Activity, String)
     */
    public static boolean permissionPermanentlyDenied(@NonNull Fragment host,
                                                      @NonNull String deniedPermission) {
        return PermissionHelper.newInstance(host).permissionPermanentlyDenied(deniedPermission);
    }

    /**
     * @see #permissionPermanentlyDenied(Activity, String).
     */
    public static boolean permissionPermanentlyDenied(@NonNull android.app.Fragment host,
                                                      @NonNull String deniedPermission) {
        return PermissionHelper.newInstance(host).permissionPermanentlyDenied(deniedPermission);
    }

    /**
     * See if some denied permission has been permanently denied.
     *
     * @param host  requesting context.
     * @param perms array of permissions.
     * @return true if the user has previously denied any of the {@code perms} and we should show a
     * rationale, false otherwise.
     */
    public static boolean somePermissionDenied(@NonNull Activity host,
                                               @NonNull String... perms) {
        return PermissionHelper.newInstance(host).somePermissionDenied(perms);
    }

    /**
     * @see #somePermissionDenied(Activity, String...)
     */
    public static boolean somePermissionDenied(@NonNull Fragment host,
                                               @NonNull String... perms) {
        return PermissionHelper.newInstance(host).somePermissionDenied(perms);
    }

    /**
     * @see #somePermissionDenied(Activity, String...)
     */
    public static boolean somePermissionDenied(@NonNull android.app.Fragment host,
                                               @NonNull String... perms) {
        return PermissionHelper.newInstance(host).somePermissionDenied(perms);
    }

    /**
     * 回调，权限全部被授予
     * <p>
     * Run permission callbacks on an object that requested permissions but already has them by
     * simulating {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @param object      the object requesting permissions.
     * @param requestCode the permission request code.
     * @param perms       a list of permissions requested.
     */
    private static void notifyAlreadyHasPermissions(@NonNull Object object,
                                                    int requestCode,
                                                    @NonNull String[] perms) {
        // 构造权限全部被授予的 int[] grantResults
        int[] grantResults = new int[perms.length];
        for (int i = 0; i < perms.length; i++) {
            grantResults[i] = PackageManager.PERMISSION_GRANTED;
        }
        // 回调权限授予情况
        onRequestPermissionsResult(requestCode, perms, grantResults, object);
    }

    /**
     * 回调 AfterPermissionGranted 注解方法
     * <p>
     * Find all methods annotated with {@link AfterPermissionGranted} on a given object with the
     * correct requestCode argument.
     *
     * @param object      the object with annotated methods.
     * @param requestCode the requestCode passed to the annotation.
     */
    private static void runAnnotatedMethods(@NonNull Object object, int requestCode) {
        Class clazz = object.getClass();
        if (isUsingAndroidAnnotations(object)) {
            clazz = clazz.getSuperclass();
        }

        while (clazz != null) {
            // 查找其对应的全部方法
            for (Method method : clazz.getDeclaredMethods()) {
                // 查找AfterPermissionGranted注解的方法
                AfterPermissionGranted ann = method.getAnnotation(AfterPermissionGranted.class);
                if (ann != null) {
                    // Check for annotated methods with matching request code.
                    // 对比注解中指定的value
                    if (ann.value() == requestCode) {
                        // 必须为无返回参数的方法
                        // Method must be void so that we can invoke it
                        if (method.getParameterTypes().length > 0) {
                            throw new RuntimeException(
                                    "Cannot execute method " + method.getName() + " because it is non-void method and/or has input parameters.");
                        }
                        //
                        try {
                            // Make method accessible if private
                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }
                            // 调用该方法
                            method.invoke(object);
                        } catch (IllegalAccessException e) {
                            Log.e(TAG, "runDefaultMethod:IllegalAccessException", e);
                        } catch (InvocationTargetException e) {
                            Log.e(TAG, "runDefaultMethod:InvocationTargetException", e);
                        }
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Determine if the project is using the AndroidAnnotations library.
     */
    private static boolean isUsingAndroidAnnotations(@NonNull Object object) {
        if (!object.getClass().getSimpleName().endsWith("_")) {
            return false;
        }
        try {
            Class clazz = Class.forName("org.androidannotations.api.view.HasViews");
            return clazz.isInstance(object);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
