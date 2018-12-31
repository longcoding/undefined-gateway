package com.longcoding.undefined.models.apis;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * Created by longcoding on 19. 1. 1..
 */

@Data
public class APISpec implements Serializable, Cloneable {

    private static final long serialVersionUID = -3037135041336335171L;

    int apiId;
    String apiName;
    List<String> protocol;
    String method;
    String inboundUrl;
    String outboundUrl;
    List<String> header;
    List<String> headerRequired;
    List<String> urlParam;
    List<String> urlParamRequired;

}
