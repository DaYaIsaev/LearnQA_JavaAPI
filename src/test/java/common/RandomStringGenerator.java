package common;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomStringGenerator {
    public static String randomString(int n) {
        Random rnd = new Random();
        Supplier<Integer> randomNumbers = () -> rnd.nextInt(26);
        String result = Stream.generate(randomNumbers)
                .limit(n)
                .map(i -> 'a' + i)
                .map(Character::toString)
                .collect(Collectors.joining());
        return result;
    }
}
