package test;

public class Magick4JTestSuite {
    public static void main(String... args) {
        new Magick4JTestSuite().run();
    }

    public void run() {
        new ImageTest().run();
    }
}
