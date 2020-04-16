package de.ice09.crypyapi.xdai.api;

public @interface Payable {

    Currency currency() default Currency.USD;
    double equivalentValue() default 0;
    StableCoin[] accepted() default StableCoin.DAI;
}
