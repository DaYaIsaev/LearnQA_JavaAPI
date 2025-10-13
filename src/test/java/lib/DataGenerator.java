package lib;

import common.RandomStringGenerator;

import java.util.HashMap;
import java.util.Map;

public class DataGenerator {

    public static String getRandomEmail(int emailNameLength, boolean domainWithSymbol) {
        //String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        String emailName = RandomStringGenerator.randomString(emailNameLength);
        String correctDomain = "@example.com";
        String incorrectDomain = "example.com";
        if(domainWithSymbol){
            return emailName + correctDomain;
        } else {
            return emailName + incorrectDomain;
        }
    }

    public static Map<String, String> getRegistrationData(int emailNameLength, boolean domainWithSymbol){
        Map<String, String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomEmail(emailNameLength,domainWithSymbol));
        data.put("password","123");
        data.put("username", "learnqa");
        data.put("firstName", "learnqa");
        data.put("lastName", "learnqa");

        return  data;
    }

    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues){
        Map<String, String> defaultValues = DataGenerator.getRegistrationData(20,true);

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for (String key : keys){
            if (nonDefaultValues.containsKey(key)) {
                userData.put(key, nonDefaultValues.get(key));
            } else {
                userData.put(key, defaultValues.get(key));
            }
        }
        return userData;
    }
}
