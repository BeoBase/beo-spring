package com.beobase.beospring.app.web;

import java.util.List;

import com.beobase.beospring.app.AppInfo;

public record AppsResponse(List<AppInfo> apps) {
}
