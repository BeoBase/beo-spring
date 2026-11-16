package com.beobase.beospring.app;

import java.util.List;

public interface AppService {

    // All apps, sorted by name
    List<AppInfo> findAllApps();
}
