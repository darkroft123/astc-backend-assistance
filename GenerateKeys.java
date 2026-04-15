import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class GenerateKeys {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());

        try (FileWriter fw = new FileWriter("privateKey.pem")) {
            fw.write("-----BEGIN PRIVATE KEY-----\n");
            fw.write(privateKey);
            fw.write("\n-----END PRIVATE KEY-----");
        }

        try (FileWriter fw = new FileWriter("publicKey.pem")) {
            fw.write("-----BEGIN PUBLIC KEY-----\n");
            fw.write(publicKey);
            fw.write("\n-----END PUBLIC KEY-----");
        }

        System.out.println("Claves generadas!");
    }
}