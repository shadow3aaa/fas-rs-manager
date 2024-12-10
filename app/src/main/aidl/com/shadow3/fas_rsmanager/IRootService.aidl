// IRootService.aidl
package com.shadow3.fas_rsmanager;

// Declare any non-default types here with import statements

interface IRootService {
    String getVersionName();
    int getVersionCode();
    boolean getWorkingStatus();
    String getConfig();
    void setConfig(String config);
}
