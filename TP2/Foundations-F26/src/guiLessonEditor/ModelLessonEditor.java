package guiLessonEditor;

import java.util.Locale;

import database.LessonsLearnedDatabase;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import validation.LessonInputPolicy.Problem;

/*******
 * <p> Title: ModelLessonEditor Class. </p>
 *
 * <p> Description: The lesson editor Model.  It turns what the contributor typed into a stored
 * lesson, either a new one (CS-1) or a change to an existing one (CS-7), and decides whether the
 * contributor is allowed to make that change (CS-9). </p>
 *
 * <p> As with ModelLessonsList, the decisions are kept in the Model, with no JavaFX, so the
 * testing automation can check exactly what the editor will do with a given set of inputs.  The
 * Controller only reads the fields, calls one of the two save methods, and reports the result.
 * </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-16  Initial version for Team Project Phase 2
 */
public class ModelLessonEditor {

	/** Source: CS-1, CS-7.  The lesson most recently stored by this Model, so the Controller can
	 *  show the contributor the lesson they just saved, with the identifier it was given. */
	private static LessonsLearned savedLesson = null;


	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public ModelLessonEditor() {
	}


	/** @return the lesson most recently stored, or null if the last save was refused */
	public static LessonsLearned getSavedLesson() { return savedLesson; }


	/*****
	 * <p> Method: Problem saveNewLesson(LessonsLearnedDatabase db, LessonsLearnedList all,
	 *      String userName, String title, String category, String keywords, String description,
	 *      String resolution) </p>
	 *
	 * <p> Description: Creates and stores a new lesson (CS-1).  The contributor is the logged in
	 * user, passed in by the Controller, never a value the user typed; in HW2 the contributor was
	 * a text field, which let anyone record a lesson in someone else's name and defeated the
	 * ownership rule before it started.  Every field is trimmed, and the keywords are tidied into
	 * one standard form, before validation, so what is checked is exactly what is stored. </p>
	 *
	 * @param db          the open lessons database
	 * @param all         the shared list of every lesson, which gains the new lesson on success
	 * @param userName    the logged in username, recorded as the contributor
	 * @param title       the title typed
	 * @param category    the category typed
	 * @param keywords    the keywords typed, which may be empty
	 * @param description the description typed
	 * @param resolution  the resolution typed, which may be empty
	 *
	 * @return null when the lesson was stored, otherwise the problem that stopped it
	 */
	public static Problem saveNewLesson(LessonsLearnedDatabase db, LessonsLearnedList all,
			String userName, String title, String category, String keywords, String description,
			String resolution) {
		savedLesson = null;
		LessonsLearned lesson = new LessonsLearned(trim(title), trim(category),
				trim(description), trim(resolution), tidyKeywords(keywords), userName);
		Problem problem = all.createLesson(db, lesson);
		if (problem == null)
			savedLesson = lesson;
		return problem;
	}


	/*****
	 * <p> Method: Problem saveChanges(LessonsLearnedDatabase db, LessonsLearnedList all,
	 *      LessonsLearned original, String userName, String title, String category,
	 *      String keywords, String description, String resolution) </p>
	 *
	 * <p> Description: Stores a contributor's changes to an existing lesson (CS-7). </p>
	 *
	 * <p> Three things are checked before anything is written.  The user must be the lesson's
	 * contributor (CS-9); the editor only offers Edit to the contributor, but the check is
	 * repeated here so that no other route can bypass it.  Something must actually have changed,
	 * so pressing Save on an untouched form does not move the "last updated" date.  And the new
	 * values must pass the same validation as a new lesson. </p>
	 *
	 * <p> The changes are applied to a copy, not to the original object.  The original is the
	 * object in the shared list; if it were changed first and the save were then refused, the
	 * list would show values the database does not hold. </p>
	 *
	 * @param db          the open lessons database
	 * @param all         the shared list of every lesson, whose entry is replaced on success
	 * @param original    the lesson as it was read
	 * @param userName    the logged in username
	 * @param title       the title in the form
	 * @param category    the category in the form
	 * @param keywords    the keywords in the form
	 * @param description the description in the form
	 * @param resolution  the resolution in the form
	 *
	 * @return null when the changes were stored, otherwise the problem that stopped them
	 */
	public static Problem saveChanges(LessonsLearnedDatabase db, LessonsLearnedList all,
			LessonsLearned original, String userName, String title, String category,
			String keywords, String description, String resolution) {
		savedLesson = null;

		if (original == null)
			return new Problem("There is no lesson to change", "Open a lesson first.");

		if (!original.isOwnedBy(userName))
			return new Problem("Only the contributor can change this lesson",
					"This lesson was contributed by " + original.getContributor()
					+ ". You can read it, but only they can edit it.");

		LessonsLearned changed = new LessonsLearned(original.getId(), trim(title), trim(category),
				trim(description), trim(resolution), tidyKeywords(keywords),
				original.getContributor(), original.getDateCreated(), original.getDateUpdated(),
				original.getReviewStatus(), original.getReviewerComment(),
				original.getHelpfulCount());

		if (sameContent(original, changed))
			return new Problem("Nothing has changed",
					"The lesson is exactly as it was, so nothing was saved.");

		Problem problem = all.updateLesson(db, changed);
		if (problem == null)
			savedLesson = changed;
		return problem;
	}


	/*****
	 * <p> Method: String tidyKeywords(String keywords) </p>
	 *
	 * <p> Description: Puts the keywords into one standard form, "first, second, third", by
	 * trimming each one and joining them with a comma and a space.  Duplicates, ignoring case,
	 * are dropped, so "javafx, JavaFX" is stored once.  An empty entry, from two commas in a row,
	 * is left in place on purpose, so that validation can tell the user about it rather than the
	 * editor quietly changing what they typed. </p>
	 *
	 * @param keywords the keywords as typed
	 *
	 * @return the keywords in standard form, or an empty string when there are none
	 */
	public static String tidyKeywords(String keywords) {
		if (keywords == null || keywords.trim().isEmpty())
			return "";
		StringBuilder result = new StringBuilder();
		java.util.Set<String> seen = new java.util.HashSet<>();
		boolean first = true;
		for (String part : keywords.split(",", -1)) {
			String keyword = part.trim();
			if (!keyword.isEmpty() && !seen.add(keyword.toLowerCase(Locale.ROOT)))
				continue;
			// Delimit by position rather than result length.  An empty first entry must remain
			// visible (", java"), otherwise validation never sees the user's leading comma.
			if (!first)
				result.append(", ");
			result.append(keyword);
			first = false;
		}
		return result.toString();
	}


	// True when every field a contributor can edit is the same in both lessons.
	private static boolean sameContent(LessonsLearned a, LessonsLearned b) {
		return same(a.getTitle(), b.getTitle()) && same(a.getCategory(), b.getCategory())
				&& same(a.getKeywords(), b.getKeywords())
				&& same(a.getDescription(), b.getDescription())
				&& same(a.getResolution(), b.getResolution());
	}

	private static boolean same(String a, String b) {
		return (a == null ? "" : a).equals(b == null ? "" : b);
	}

	private static String trim(String s) {
		return s == null ? "" : s.trim();
	}
}
