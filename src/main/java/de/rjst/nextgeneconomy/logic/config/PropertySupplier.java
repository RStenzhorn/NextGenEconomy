package de.rjst.nextgeneconomy.logic.config;


import de.rjst.nextgeneconomy.setting.NgeSetting;

@FunctionalInterface
public interface PropertySupplier {

    <T> T apply(NgeSetting setting, Class<T> type);

}


