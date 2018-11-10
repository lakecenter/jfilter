package com.json.ignore.advice;

import com.json.ignore.filter.FilterFields;
import com.json.ignore.filter.dynamic.DynamicFilterComponent;
import com.json.ignore.filter.dynamic.DynamicFilterEvent;
import com.json.ignore.filter.dynamic.DynamicFilter;
import com.json.ignore.filter.dynamic.DynamicSessionFilter;
import com.json.ignore.request.RequestSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dynamic filter provider
 *
 * <p>This component finds and provides Dynamic filters which annotated by {@link DynamicFilterComponent} annotation.
 * Could be used for retrieving {@link FilterFields} from {@link MethodParameter} which annotated by {@link DynamicFilter}
 */
@Component
public class DynamicFilterProvider {
    private final ApplicationContext applicationContext;
    private final Map<Class, DynamicFilterEvent> dynamicFilterMap;

    /**
     * Creates a new instance of the {@link DynamicFilterProvider} class.
     *
     * @param applicationContext {@link ApplicationContext} bean
     */
    @Autowired
    public DynamicFilterProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.dynamicFilterMap = new ConcurrentHashMap<>();
        findDynamicFilters();
    }

    /**
     * Find dynamic filter beans
     *
     * <p>Attempts to find all components which annotated by {@link DynamicFilterComponent}
     * and inherited from {@link DynamicFilterEvent}
     * For example of component please see {@link DynamicSessionFilter}
     */
    private void findDynamicFilters() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(DynamicFilterComponent.class);

        beans.forEach((k, v) -> {
            if (DynamicFilterEvent.class.isInstance(v))
                dynamicFilterMap.put(v.getClass(), (DynamicFilterEvent) v);
        });
    }

    /**
     * Check if method has {@link DynamicFilter} annotation
     *
     * @param methodParameter method parameter
     * @return true if annotation is found, otherwise false
     */
    public static boolean isAccept(MethodParameter methodParameter) {
        return methodParameter.getMethod().getDeclaredAnnotation(DynamicFilter.class) != null;
    }

    /**
     * Returns {@link FilterFields} from annotated method
     *
     * <p>Attempts to find and return dynamic filter from dynamicFilterMap
     *
     * @param methodParameter method parameter
     * @param request service request
     * @return if found dynamic filter returns {@link FilterFields}, otherwise empty FilterFields
     */
    public FilterFields getFields(MethodParameter methodParameter, RequestSession request) {
        DynamicFilter dynamicFilterAnnotation = methodParameter.getMethod().getDeclaredAnnotation(DynamicFilter.class);

        if (dynamicFilterAnnotation != null && dynamicFilterMap.containsKey(dynamicFilterAnnotation.value())) {
            DynamicFilterEvent filter = dynamicFilterMap.get(dynamicFilterAnnotation.value());
            return filter.onGetFilterFields(methodParameter, request);
        } else
            return new FilterFields();
    }
}
