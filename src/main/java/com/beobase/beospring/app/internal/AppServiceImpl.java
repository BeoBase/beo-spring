package com.beobase.beospring.app.internal;

import java.util.List;

import com.beobase.beospring.app.AppInfo;
import com.beobase.beospring.app.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AppServiceImpl implements AppService {

    private final AppRepository appRepository;

    // TODO: sorted by name for now so the order is stable. Add a sort_order column to choose the page order.
    @Override
    public List<AppInfo> findAllApps() {
        return appRepository.findAll(Sort.by("name")).stream()
                .map(this::toInfo)
                .toList();
    }

    private AppInfo toInfo(App app) {
        return new AppInfo(
                app.getId(),
                app.getName(),
                List.copyOf(app.getTags()),
                app.getRoute(),
                app.getRouteType().name()
        );
    }
}
