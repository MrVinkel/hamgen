package org.hamgen.testtools.matcher;

import org.hamgen.HamGenDiagnosingMatcher;
import org.hamgen.model.MatcherField;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.*;

public class MatcherFieldMatcher extends HamGenDiagnosingMatcher {
    private Matcher nameMatcher;
    private Matcher getterNameMatcher;
    private Matcher typeMatcher;
    private Matcher postFixMatcher;

    public MatcherFieldMatcher(MatcherField expected) {
        this.nameMatcher = expected.getName() == null || expected.getName().isEmpty() ? isEmptyOrNullString() : is(expected.getName());
        this.getterNameMatcher = expected.getGetterName() == null || expected.getGetterName().isEmpty() ? isEmptyOrNullString() : is(expected.getGetterName());
        this.postFixMatcher = expected.getFieldPostFix() == null || expected.getFieldPostFix().isEmpty() ? isEmptyOrNullString() : is(expected.getFieldPostFix());
        this.typeMatcher = expected.getType() == null ? nullValue() : isA(expected.getType().getClass());
    }

    @Override
    protected boolean matchesSafely(Object item, Description mismatchDesc) {
        boolean matches = true;
        MatcherField actual = (MatcherField) item;
        mismatchDesc.appendText("{");
        if (!nameMatcher.matches(actual.getName())) {
            reportMismatch("name", nameMatcher, actual.getName(), mismatchDesc, matches);
            matches = false;
        }
        if (!getterNameMatcher.matches(actual.getGetterName())) {
            reportMismatch("getterName", getterNameMatcher, actual.getGetterName(), mismatchDesc, matches);
            matches = false;
        }
        if (!postFixMatcher.matches(actual.getFieldPostFix())) {
            reportMismatch("fieldPostFix", postFixMatcher, actual.getFieldPostFix(), mismatchDesc, matches);
            matches = false;
        }
        if (!typeMatcher.matches(actual.getType())) {
            reportMismatch("type", typeMatcher, actual.getType(), mismatchDesc, matches);
            matches = false;
        }
        mismatchDesc.appendText("}");
        return matches;
    }

    @Override
    public void describeTo(Description desc) {
        desc.appendText("{");
        desc.appendText("name ");
        desc.appendDescriptionOf(nameMatcher);
        desc.appendText(", ");
        desc.appendText("getterName ");
        desc.appendDescriptionOf(getterNameMatcher);
        desc.appendText(", ");
        desc.appendText("fieldPostFix ");
        desc.appendDescriptionOf(postFixMatcher);
        desc.appendText(", ");
        desc.appendText("type ");
        desc.appendDescriptionOf(typeMatcher);
        desc.appendText("}");
    }

    @Factory
    public static MatcherFieldMatcher isMatcherField(MatcherField expected) {
        return new MatcherFieldMatcher(expected);
    }
}
