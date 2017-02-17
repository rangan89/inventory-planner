package fk.retail.ip.manager.config;

import org.hibernate.cfg.ImprovedNamingStrategy;

public class AnnotationRespectfulNamingStrategy extends ImprovedNamingStrategy {

    public static final AnnotationRespectfulNamingStrategy INSTANCE = new AnnotationRespectfulNamingStrategy();

    @Override
    public String tableName(String tableName) {
        return tableName;
    }

    @Override
    public String columnName(String columnName) {
        return columnName;
    }
}
