package com.beobase.beospring.app;

import java.util.List;

// routeType is "INTERNAL" (a page inside the React app) or "EXTERNAL" (another website)
public record AppInfo(String id, String name, List<String> tags, String route, String routeType) {
}
