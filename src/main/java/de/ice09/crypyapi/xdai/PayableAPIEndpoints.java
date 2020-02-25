package de.ice09.crypyapi.xdai;

import org.springframework.web.bind.annotation.RestController;
import java.net.http.HttpResponse;
import static de.ice09.crypyapi.xdai.Currency.USD;

@RestController
public class PayableAPIEndpoints<T> {

    @Payable(currency = USD, equivalentValue = 0.1, accepted = {StableCoin.DAI, StableCoin.XDAI})
    public byte[] createPicture() {
        return pictureService.create();
    }

    @Payable(currency = USD, equivalentValue = 1.00, accepted = {StableCoin.DAI, StableCoin.XDAI})
    public void chargeMe(String address) {
        chargingService.charge(address);
    }

    @Payable(currency = USD, equivalentValue = 0.001, accepted = {StableCoin.DAI, StableCoin.XDAI})
    public long getTemperature() {
        return sensor.getTemperature();
    }

    @Payable(currency = USD, equivalentValue = 0.01, accepted = {StableCoin.DAI, StableCoin.XDAI})
    public void flyPackageToDestination(long longitude, long latitude) {
        drone.flyTo(longitude, latitude);
    }



    /** Left as exercise :) */

    PictureService pictureService = null;
    ChargingService chargingService = null;
    Sensor sensor = null;
    Drone drone = null;

}
