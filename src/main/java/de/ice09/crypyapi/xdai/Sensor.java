package de.ice09.crypyapi.xdai;

public class Sensor {
    public long getTemperature() {
        return 0;
    }
    // sign
					/*
					Sign.SignatureData signature = Sign.signMessage(random32, credentials.getEcKeyPair(), false);
					ByteBuffer sigBuffer = ByteBuffer.allocate(signature.getR().length + signature.getS().length + 1);
					sigBuffer.put(signature.getR());
					sigBuffer.put(signature.getS());
					sigBuffer.put(signature.getV());
					log.info(String.format("signed proof: %s", Numeric.toHexString(sigBuffer.array())));
					 */
}
