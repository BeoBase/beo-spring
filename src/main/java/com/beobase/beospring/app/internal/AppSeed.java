package com.beobase.beospring.app.internal;

import java.util.List;

// One entry of the apps-seed.json file (no id: the backend generates it)
record AppSeed(String name, List<String> tags, String route, RouteType routeType) {
}
