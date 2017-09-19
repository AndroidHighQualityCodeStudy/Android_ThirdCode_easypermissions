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
package pub.devrel.easypermissions.sample;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";

    /**
     *
     */
    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_LOCATION_CONTACTS_PERM = 124;


    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 摄像机 权限
        findViewById(R.id.button_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraTask();
            }
        });

        // 位置、联系人 权限
        findViewById(R.id.button_location_and_contacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationAndContactsTask();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 权限返回，走到这里
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // ########################################摄像机权限##################################################

    /**
     * 摄像机权限
     */
    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraTask() {
        // 判断是否有摄像机权限
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // 有摄像机权限
            Toast.makeText(this, "TODO: Camera things", Toast.LENGTH_LONG).show();
        } else {
            // 请求摄像机权限
            //第二个参数是 被拒绝后再次申请该权限的解释
            //第三个参数是 请求码
            //第四个参数是 要申请的权限
            EasyPermissions.requestPermissions(this,
                    getString(R.string.rationale_camera),
                    RC_CAMERA_PERM,
                    Manifest.permission.CAMERA);
        }
    }

    // ###########################################位置和联系人权限###############################################

    private static final String[] LOCATION_AND_CONTACTS =
            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS};

    /**
     * 位置和联系人权限
     */
    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    public void locationAndContactsTask() {
        // 位置 和 联系人权限
        if (EasyPermissions.hasPermissions(this, LOCATION_AND_CONTACTS)) {
            Toast.makeText(this, "TODO: Location and Contacts things", Toast.LENGTH_LONG).show();
        } else {
            // 请求位置 和 联系人权限
            //第二个参数是 被拒绝后再次申请该权限的解释
            //第三个参数是 请求码
            //第四个参数是 要申请的权限
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_location_contacts),
                    RC_LOCATION_CONTACTS_PERM,
                    LOCATION_AND_CONTACTS);
        }
    }

    // ############################################权限是否授予##############################################
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("xiaxl: ", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("xiaxl: ", "onPermissionsDenied:" + requestCode + ":" + perms.size());
        Log.d("xiaxl: ", "perms: " + perms);


        // 点击  权限弹窗上 "不再询问"按钮后，要求进入设置页面，开启权限
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    // ##########################################设置中返回后################################################
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("xiaxl: ", "MainActivity onActivityResult: " + resultCode);

        // 设置返回后，判断权限的获取情况
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            String yes = getString(R.string.yes);
            String no = getString(R.string.no);

            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(
                    this,
                    getString(R.string.returned_from_app_settings_to_activity,
                            hasCameraPermission() ? yes : no,
                            hasLocationAndContactsPermissions() ? yes : no,
                            hasSmsPermission() ? yes : no),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }


    // ##########################################################################################

    private boolean hasCameraPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA);
    }

    private boolean hasLocationAndContactsPermissions() {
        return EasyPermissions.hasPermissions(this, LOCATION_AND_CONTACTS);
    }

    private boolean hasSmsPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_SMS);
    }
}
