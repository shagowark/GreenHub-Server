package ru.greenhubserver.utils;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "minio.image")
@Data
public class ImagePropertiesUtil {
    private String defaultImagePath;
    private String defaultImageName;
    private String activeImagePath;
    private String activeImageName;
    private String bicycleImagePath;
    private String bicycleImageName;
    private String veganImagePath;
    private String veganImageName;
    private String volonterImagePath;
    private String volonterImageName;
    private String zhivotniImagePath;
    private String zhivotniImageName;

    private List<ImageProperty> properties = new ArrayList<>();

    @PostConstruct
    private void saveToList(){
        properties.add(new ImageProperty(defaultImagePath, defaultImageName));
        properties.add(new ImageProperty(activeImagePath, activeImageName));
        properties.add(new ImageProperty(bicycleImagePath, bicycleImageName));
        properties.add(new ImageProperty(veganImagePath, veganImageName));
        properties.add(new ImageProperty(volonterImagePath, volonterImageName));
        properties.add(new ImageProperty(zhivotniImagePath, zhivotniImageName));
    }


}
