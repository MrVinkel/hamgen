package dk.martinvinkel.hamgen;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.internal.ReflectiveTypeFinder;


public abstract class HamGenDiagnosingMatcher<T> extends BaseMatcher<T> {
    private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("matchesSafely", 2, 0);
    private final Class<?> expectedType;

    protected abstract boolean matchesSafely(T item, Description mismatchDescription);

    protected HamGenDiagnosingMatcher(Class<?> expectedType) {
        this.expectedType = expectedType;
    }

    protected HamGenDiagnosingMatcher(ReflectiveTypeFinder typeFinder) {
        this.expectedType = typeFinder.findExpectedType(getClass());
    }

    protected HamGenDiagnosingMatcher() {
        this(TYPE_FINDER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final boolean matches(Object item) {
        return expectedType.isInstance(item) && matchesSafely((T) item, new Description.NullDescription());
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void describeMismatch(Object item, Description mismatchDescription) {
        if (!expectedType.isInstance(item)) {
            super.describeMismatch(item, mismatchDescription);
        } else {
            matchesSafely((T) item, mismatchDescription);
        }
    }

    private static void reportMismatch(String name, Matcher<?> matcher, Object item, Description mismatchDescription, boolean firstMismatch) {
        if (!firstMismatch) {
            mismatchDescription.appendText(", ");
        }
        mismatchDescription.appendText(name).appendText(" ");
        matcher.describeMismatch(item, mismatchDescription);
    }
}
