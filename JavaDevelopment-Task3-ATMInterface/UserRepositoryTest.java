public class UserRepositoryTest {

    public static void main(String[] args) {

        UserRepository repository = new UserRepository();

        System.out.println("Test 1 - Correct credentials: "
                + repository.credentialsMatch("USER001", "1234"));

        System.out.println("Test 2 - Incorrect PIN: "
                + repository.credentialsMatch("USER001", "9999"));

        System.out.println("Test 3 - Unregistered User ID: "
                + repository.credentialsMatch("USER999", "1234"));

        System.out.println("Test 4 - Lowercase User ID: "
                + repository.credentialsMatch("user001", "1234"));
    }
}