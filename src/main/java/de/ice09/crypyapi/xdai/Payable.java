package de.ice09.crypyapi.xdai;

public @interface Payable {

    Currency currency() default Currency.USD;
    double equivalentValue() default 0;
    StableCoin[] accepted() default StableCoin.DAI;
}
