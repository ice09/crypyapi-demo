package de.ice09.crypyapi.xdai;

import io.reactivex.Flowable;
import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
@Log
public class XdaiApplication implements CommandLineRunner {

	private AtomicBoolean nextOnePlease;
	private String randomId;

	public static void main(String[] args) {
		SpringApplication.run(XdaiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Web3j httpWeb3 = Web3j.build(new HttpService("https://dai.poa.network"));
		// Use well known private key, not to be used in production or with transaction values greater than $1
		String privateKey = "710404145a788a5f2b7b6678f894a8ba621bdf8f4c04b44a3f703159916d39df";
		Credentials credentials = Credentials.create(privateKey);
		System.out.println("\nSECURITY NOTICE: In case something goes wrong, use this private key to recover money.\nBut better be fast, this private key is well known: " + privateKey);
		System.out.println("\nHowdy! I am serving the best Chuck Norris jokes ever, but it will cost you $0.0001 (though I don't check it, feel free to send less).");
		String addressToCheck = credentials.getAddress();

		tellJokes(httpWeb3, addressToCheck);

		while (true) {
			randomId = RandomStringUtils.randomAlphabetic(6);
			System.out.println("\nFor the next joke:");
			System.out.println("* Sign this identifier with your private key: \"" + randomId + "\"");
			System.out.println("* Send $0.0001 to address " + addressToCheck + " and add the signed identifier as metadata");
			System.out.println("The format for the extradata (message) is \"" + randomId + "|" + "0x...\" Note: Without the signed identifier, I am unable to correlate your payment!");

			nextOnePlease = new AtomicBoolean(false);
			while (!nextOnePlease.get()) {
				Thread.sleep(1000);
			}
		}

	}

	private void tellJokes(Web3j httpWeb3, String addressToCheck) {
		httpWeb3.transactionFlowable().onErrorResumeNext(Flowable.empty()).subscribe(tx -> {
			String input = tx.getInput();
			String value = (tx.getValue() != null) ? tx.getValue().toString() : BigDecimal.ZERO.toString();
			String from = tx.getFrom();
			String to = tx.getTo();
			if (StringUtils.isNotEmpty(to) && to.equals(addressToCheck)) {
				String plainInput = new String(Hex.decode(input.substring(2).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
				System.out.println(("\tGot transaction from " + from + " with input " + plainInput));

				String[] inputAndSignature = plainInput.split("\\|");
				if (inputAndSignature.length == 2) {
					if (randomId.equals(inputAndSignature[0])) {
						byte[] proof = Hash.sha3(("\u0019Ethereum Signed Message:\n6" + randomId).getBytes(StandardCharsets.UTF_8));
						String ecrecovered = ecrecoverAddress(proof, HexUtils.fromHexString(inputAndSignature[1].substring(2)), from);

						if (StringUtils.isNotEmpty(ecrecovered)) {
							System.out.println("\nThanks! Enjoy the best Chuck Norris joke ever: " + Resource.getBestJokeEver());
							nextOnePlease.set(true);
						} else {
							System.out.println("\tERROR: Recovered address and sender do not match - sorry, no joke for you!");
						}
					} else {
						System.out.println("\tERROR: Wrong payload, transaction input doesn't match transaction identifier.");
					}
				}
			}
		});
	}

	public String ecrecoverAddress(byte[] proof, byte[] signature, String expectedAddress) {
		ECDSASignature esig = new ECDSASignature(Numeric.toBigInt(Arrays.copyOfRange(signature, 0, 32)), Numeric.toBigInt(Arrays.copyOfRange(signature, 32, 64)));
		BigInteger res;
		for (int i=0; i<4; i++) {
			res = Sign.recoverFromSignature(i, esig, proof);
			if ((res != null) && Keys.getAddress(res).toLowerCase().equals(expectedAddress.substring(2).toLowerCase())) {
				log.info("public Ethereum address: 0x" + Keys.getAddress(res));
				return Keys.getAddress(res);
			}
		}
		return null;
	}
}
