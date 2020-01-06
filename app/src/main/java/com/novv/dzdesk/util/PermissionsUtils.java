package com.novv.dzdesk.util;

import android.Manifest;
import android.content.Context;
import com.ark.utils.permissions.PermissionChecker;
import com.ark.utils.permissions.PermissionItem;
import com.ark.utils.permissions.PermissionSimpleCallback;
import java.util.ArrayList;
import java.util.List;

public class PermissionsUtils {

  public static void checkSplash(Context context, final OnPermissionBack permissionBack) {
    if (context == null) {
      return;
    }
    List<PermissionItem> list = new ArrayList<>();
    list.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "读取文件"));
    list.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储文件"));
    list.add(new PermissionItem(Manifest.permission.READ_PHONE_STATE, "设备识别码"));
    PermissionChecker.create(context)
        .permissions(list)
        .checkMultiPermission(new PermissionSimpleCallback() {
          @Override public void onClose() {
            if (permissionBack != null) {
              permissionBack.onResult(false);
            }
          }

          @Override public void onFinish() {
            if (permissionBack != null) {
              permissionBack.onResult(true);
            }
          }
        });
  }

  public static void checkStorage(Context context, final OnPermissionBack permissionBack) {
    if (context == null) {
      return;
    }
    List<PermissionItem> list = new ArrayList<>();
    list.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "读取文件"));
    list.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储文件"));
    PermissionChecker.create(context)
        .permissions(list)
        .checkMultiPermission(new PermissionSimpleCallback() {
          @Override public void onClose() {
            if (permissionBack != null) {
              permissionBack.onResult(false);
            }
          }

          @Override public void onFinish() {
            if (permissionBack != null) {
              permissionBack.onResult(true);
            }
          }
        });
  }

  public static void checkCamera(Context context, final OnPermissionBack permissionBack) {
    if (context == null) {
      return;
    }
    List<PermissionItem> list = new ArrayList<>();
    list.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "读取文件"));
    list.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储文件"));
    list.add(new PermissionItem(Manifest.permission.CAMERA, "拍照"));
    PermissionChecker.create(context)
        .permissions(list)
        .checkMultiPermission(new PermissionSimpleCallback() {
          @Override public void onClose() {
            if (permissionBack != null) {
              permissionBack.onResult(false);
            }
          }

          @Override public void onFinish() {
            if (permissionBack != null) {
              permissionBack.onResult(true);
            }
          }
        });
  }

  public interface OnPermissionBack {

    void onResult(boolean success);
  }
}
