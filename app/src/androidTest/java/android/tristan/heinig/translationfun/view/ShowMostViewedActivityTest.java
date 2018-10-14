package android.tristan.heinig.translationfun.view;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.tristan.heinig.translationfun.MainActivity;
import android.tristan.heinig.translationfun.R;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ShowMostViewedActivityTest {

  @Rule
  public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

  private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup && parentMatcher.matches(parent) && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }

  @Test
  public void switchActivitiesTest() {
    clickOnViewsButton();
    // Added a sleep statement to match the app's execution delay.
    // The recommended way to handle such scenarios is to use Espresso idling resources:
    // https://developer.android.com/training/testing/espresso/idling-resource
    waitAMoment();
    isRecyclerViewDisplayed();
    backToMainActivity();
    waitAMoment();
    isButtonDisplayed();
  }

  private void clickOnViewsButton() {
    ViewInteraction appCompatButton2 = onView(allOf(ViewMatchers.withId(R.id.btn_most_viewed), withText("Show All"),
      childAtPosition(childAtPosition(withClassName(is("android.support.v7.widget.CardView")), 0), 2)));
    appCompatButton2.perform(scrollTo(), click());
  }

  private void isButtonDisplayed() {
    ViewInteraction button = onView(allOf(withId(R.id.btn_recent),
      childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class), 0), 2), isDisplayed()));
    button.check(matches(isDisplayed()));
  }

  private void backToMainActivity() {
    ViewInteraction appCompatImageButton = onView(allOf(withContentDescription("Nach oben"),
      childAtPosition(allOf(withId(R.id.action_bar), childAtPosition(withId(R.id.action_bar_container), 0)), 1), isDisplayed()));
    appCompatImageButton.perform(click());
  }

  private void isRecyclerViewDisplayed() {
    ViewInteraction recyclerView = onView(
      allOf(withId(R.id.recycler_view), childAtPosition(childAtPosition(withId(android.R.id.content), 0), 0), isDisplayed()));
    recyclerView.check(matches(isDisplayed()));
  }

  private void waitAMoment() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
