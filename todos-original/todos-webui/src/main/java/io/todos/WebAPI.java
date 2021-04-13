package io.todos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class WebAPI {

    private static final Logger LOG = LoggerFactory.getLogger(WebAPI.class);

    private Environment env;

    private BuildProperties buildProperties;

    @Autowired
    public WebAPI(Environment environment, BuildProperties buildProperties) {
        this.env = environment;
        this.buildProperties = buildProperties;
    }

    @GetMapping("/metadata")
    public List<Metadata> metadata() {
        Map<String, Object> allProperties = new HashMap<>();
        for(PropertySource<?> ps : ((AbstractEnvironment)env).getPropertySources()) {
            if(ps instanceof MapPropertySource) {
                allProperties.putAll(((MapPropertySource)ps).getSource());
            }
        }
        return allProperties.entrySet().stream()
            .filter(it -> !StringUtils.isEmpty(it.getValue()))
            .sorted(Map.Entry.comparingByKey())
            .map(it -> Metadata.builder()
                    .property(it.getKey())
                    .value(it.getValue().toString()).build())
            .collect(Collectors.toList());
    }

    @GetMapping("/about")
    public BuildProperties buildInfo() {
        return buildProperties;
    }

    @GetMapping("/logs")
    public String logs() {
        return "Online";
    }

    @PostMapping("/logs")
    public void consoleLog(@RequestBody ConsoleLog consoleLog) {
        if("ERROR".equalsIgnoreCase(consoleLog.getLevel())) {
            LOG.error(consoleLog.getLine());
        } else if("WARN".equalsIgnoreCase(consoleLog.getLevel())) {
            LOG.warn(consoleLog.getLine());
        } else if("INFO".equalsIgnoreCase(consoleLog.getLevel())) {
            LOG.info(consoleLog.getLine());
        } else if("DEBUG".equalsIgnoreCase(consoleLog.getLevel())) {
            LOG.debug(consoleLog.getLine());
        } else if("TRACE".equalsIgnoreCase(consoleLog.getLevel())) {
            LOG.trace(consoleLog.getLine());
        }
    }

}
