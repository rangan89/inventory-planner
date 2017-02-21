package fk.retail.ip.zulu.internal.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by nidhigupta.m on 03/02/17.
 */

@Getter
@Setter
public class EntityView {
    String entityId;
    long entityVersion;
    String entityType;
    String entitySubType;
    String viewName;
    long viewDefinitionVersion;
    long viewVersion;
    Map<Object, Object> view;
}
