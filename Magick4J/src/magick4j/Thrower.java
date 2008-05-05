package magick4j;

public class Thrower {
    public static RuntimeException throwAny(Throwable throwable) {
        throw throwSneaky(throwable, RuntimeException.class);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> RuntimeException throwSneaky(Throwable throwable, Class<E> class_) throws E {
        throw (E) throwable;
    }
}
