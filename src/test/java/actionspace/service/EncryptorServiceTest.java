package actionspace.service;

import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class EncryptorServiceTest {
    private EncryptorService encryptorService;

    @Before
    public void init() {
        encryptorService = new EncryptorService();
    }

    @Test
    public void encrypt() {
        String stringToEncrypt = "{\"expires\":\"2019-02-11T12:49:15.540Z\",\"name\":\"sannergata\",\"inviter\":\"user\",\"projectId\":\"1\",\"email\":\"roar.granevang@gmail.com\"}";
        String encryptedString = encryptorService.encrypt(stringToEncrypt);
        assertThat(encryptedString, is(not(stringToEncrypt)));
        String decryptedString = encryptorService.decrypt(encryptedString);
        assertThat(stringToEncrypt, is(decryptedString));
    }

    @Test
    public void encryptWithUrlEncode() throws UnsupportedEncodingException {
        String stringToEncrypt = "{\"expires\":\"2019-02-11T12:49:15.540Z\",\"name\":\"sannergata\",\"inviter\":\"user\",\"projectId\":\"1\",\"email\":\"roar.granevang@gmail.com\"}";
        String encryptedString = encryptorService.encrypt(stringToEncrypt);
        String urlEncoded = URLEncoder.encode(encryptedString, "UTF-8");
        String decode = URLDecoder.decode(urlEncoded, "UTF-8");
        String decryptedString = encryptorService.decrypt(decode);
        assertThat(stringToEncrypt, is(decryptedString));
    }

}