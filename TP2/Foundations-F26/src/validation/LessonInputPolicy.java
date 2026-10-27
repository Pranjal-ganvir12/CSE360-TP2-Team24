package validation;

/*******
 * <p> Title: LessonInputPolicy Class </p>
 *
 * <p> Description: Holds the checks applied to the fields of a lesson learned before that lesson
 * is written to the database, in the order they are applied.  It is the lessons learned
 * counterpart of the supplied ScreenInputPolicy class and follows the same shape: a small Problem
 * record describing one refused input, and a method per field that returns the first problem it
 * finds or null when the input is acceptable. </p>
 *
 * <p> The limits live here rather than being scattered through the create and update code so that
 * the create path and the update path cannot drift apart.  Both call validateLesson, so a lesson
 * that would be refused on the way in cannot slip through on an edit (Contributor story CS-7).
 * Length is always checked before anything else, for the reason given in InputLimits: a single
 * long paste should be refused cheaply before any other work is done on it (CS-11). </p>
 *
 * <p> This class has no JavaFX reference and does no database work, so the testing automation can
 * exercise every rule directly. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  HW2 and TP2 additions by CSE 360 Team 24: Arush
 * Ugal, Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-05  Initial version for Individual Homework 2
 * @version 2.00  2026-10-14  TP2: keywords field, long text ceiling, keyword format rules
 */
public class LessonInputPolicy {

	/** A title has to be short enough to read at a glance in the list (CS-2). */
	public static final int MAX_TITLE = 80;

	/** A category is a single short label used for grouping and filtering (CS-5). */
	public static final int MAX_CATEGORY = 30;

	/** The description holds the body of the lesson and is allowed to be long. */
	public static final int MAX_DESCRIPTION = 2000;

	/** The resolution is the reply that resolved the lesson, also allowed to be long. */
	public static final int MAX_RESOLUTION = 2000;

	/** The keywords are a short comma separated list used by the search (CS-4). */
	public static final int MAX_KEYWORDS = 200;

	/** No single keyword may be longer than this. */
	public static final int MAX_ONE_KEYWORD = 30;

	/** No lesson may carry more keywords than this, so one lesson cannot match every search. */
	public static final int MAX_KEYWORD_COUNT = 10;

	/** The contributor is a username, so it uses the same limit usernames use elsewhere. */
	public static final int MAX_CONTRIBUTOR = InputLimits.MAX_USERNAME;

	/**
	 * The cheap ceiling for the two long text fields.
	 *
	 * <p> TP2 correction.  In HW2 the description and the resolution were checked with
	 * InputLimits.checkLength, which applies InputLimits.ABSOLUTE_MAX of 256 characters before it
	 * looks at the field's own limit.  The result was that any description longer than 256
	 * characters was refused as "far too long", even though the stated limit was 2000.  This was
	 * found while writing the TP2 boundary tests: a 2000 character description, which the
	 * documentation said was acceptable, was refused.  The long fields now have their own,
	 * larger ceiling, so a huge paste is still refused cheaply but a real lesson is not. </p>
	 */
	public static final int LONG_TEXT_ABSOLUTE_MAX = 10000;


	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public LessonInputPolicy() {
	}


	/*****
	 * <p> Record: Problem </p>
	 *
	 * <p> Description: One refused input, in the two parts a screen needs to describe it: a short
	 * heading and the detail underneath.  A null Problem means the input was acceptable, which
	 * keeps the test for "was anything wrong" to a single comparison at every call site.  This
	 * matches the Problem record used by the supplied ScreenInputPolicy class. </p>
	 *
	 * @param title  the short heading, for example "The title is required"
	 * @param detail the explanation shown underneath
	 */
	public record Problem(String title, String detail) {
	}


	/*****
	 * <p> Method: Problem validateLesson(String title, String category, String description,
	 *      String resolution, String keywords, String contributor) </p>
	 *
	 * <p> Description: Checks every field of a lesson in a fixed order, used by both the create
	 * and the update operations.  The order is the order the fields appear on the lesson editor
	 * screen, so the first problem a user is told about is the first field they would read. </p>
	 *
	 * @param title       the heading the user typed
	 * @param category    the category the user typed
	 * @param description the body of the lesson
	 * @param resolution  the reply that resolved the lesson, which may be empty
	 * @param keywords    the comma separated keywords, which may be empty
	 * @param contributor the username contributing the lesson
	 *
	 * @return null when every field is acceptable, otherwise the first problem found
	 */
	public static Problem validateLesson(String title, String category, String description,
			String resolution, String keywords, String contributor) {

		Problem problem = validateTitle(title);
		if (problem != null)
			return problem;

		problem = validateCategory(category);
		if (problem != null)
			return problem;

		problem = validateKeywords(keywords);
		if (problem != null)
			return problem;

		problem = validateDescription(description);
		if (problem != null)
			return problem;

		problem = validateResolution(resolution);
		if (problem != null)
			return problem;

		return validateContributor(contributor);
	}


	/*****
	 * <p> Method: Problem validateTitle(String title) </p>
	 *
	 * <p> Description: The title is required because it is the part of a lesson shown in the
	 * list, so a lesson with no title could not be told apart from another (CS-10).  A title made
	 * only of spaces is treated as empty, because it would show as a blank line in the list. </p>
	 *
	 * @param title the heading the user typed
	 *
	 * @return null when the title is acceptable, otherwise the problem found
	 */
	public static Problem validateTitle(String title) {
		if (isBlank(title))
			return new Problem("The title is required",
					"Enter a short title so the lesson can be told apart from the others.");

		String lengthProblem = InputLimits.checkLength(title, MAX_TITLE, "Title");
		if (!lengthProblem.isEmpty())
			return new Problem("The title is too long", lengthProblem);

		return null;
	}


	/*****
	 * <p> Method: Problem validateCategory(String category) </p>
	 *
	 * <p> Description: The category is required because the filter by category (CS-5) has
	 * nothing to group a lesson under if it is blank. </p>
	 *
	 * @param category the category the user typed
	 *
	 * @return null when the category is acceptable, otherwise the problem found
	 */
	public static Problem validateCategory(String category) {
		if (isBlank(category))
			return new Problem("The category is required",
					"Enter a category, for example Setup or Database, so the lesson can be "
					+ "grouped and found later.");

		// This exact label is the list screen's sentinel for disabling its category filter.
		// Storing it as real data would make that category impossible to select by itself.
		if ("All categories".equalsIgnoreCase(category.trim()))
			return new Problem("The category name is reserved",
					"Choose a category other than All categories. That name is used by the "
					+ "list screen to turn category filtering off.");

		String lengthProblem = InputLimits.checkLength(category, MAX_CATEGORY, "Category");
		if (!lengthProblem.isEmpty())
			return new Problem("The category is too long", lengthProblem);

		return null;
	}


	/*****
	 * <p> Method: Problem validateKeywords(String keywords) </p>
	 *
	 * <p> Description: Keywords are optional.  When they are given they must be a comma separated
	 * list of at most MAX_KEYWORD_COUNT words, each no longer than MAX_ONE_KEYWORD characters and
	 * made only of letters, digits, spaces, hyphens, full stops, and plus signs.  The character
	 * rule exists because keywords are matched by the search (CS-4); punctuation such as a
	 * semicolon is almost always a mistyped separator, and a keyword containing one would never
	 * be matched by what a user types.  The plus sign and the full stop are allowed so that names
	 * such as C++ and Node.js can be keywords. </p>
	 *
	 * @param keywords the comma separated keywords, which may be empty
	 *
	 * @return null when the keywords are acceptable, otherwise the problem found
	 */
	public static Problem validateKeywords(String keywords) {
		if (isBlank(keywords))
			return null;

		String lengthProblem = InputLimits.checkLength(keywords, MAX_KEYWORDS, "Keywords");
		if (!lengthProblem.isEmpty())
			return new Problem("The keywords are too long", lengthProblem);

		// Preserve a trailing empty entry so "java," is refused just like ",java" and
		// "java,,database".  String.split without a negative limit silently discards it.
		String[] parts = keywords.split(",", -1);
		if (parts.length > MAX_KEYWORD_COUNT)
			return new Problem("There are too many keywords",
					"A lesson can have at most " + MAX_KEYWORD_COUNT + " keywords. You entered "
					+ parts.length + ".");

		for (String part : parts) {
			String keyword = part.trim();

			// An empty entry comes from two commas in a row or a trailing comma.  It is refused
			// rather than silently dropped so the user sees exactly what will be stored.
			if (keyword.isEmpty())
				return new Problem("A keyword is empty",
						"Separate keywords with single commas, for example: javafx, setup, mac");

			if (keyword.length() > MAX_ONE_KEYWORD)
				return new Problem("A keyword is too long",
						"The keyword \"" + keyword + "\" is " + keyword.length()
						+ " characters. Each keyword must be " + MAX_ONE_KEYWORD
						+ " characters or fewer.");

			for (int i = 0; i < keyword.length(); i++) {
				char c = keyword.charAt(i);
				if (!(Character.isLetterOrDigit(c) || c == ' ' || c == '-' || c == '.'
						|| c == '+'))
					return new Problem("A keyword contains a character that is not allowed",
							"The keyword \"" + keyword + "\" contains '" + c + "'. Use letters, "
							+ "digits, spaces, hyphens, full stops, and plus signs, and "
							+ "separate keywords with commas.");
			}
		}
		return null;
	}


	/*****
	 * <p> Method: Problem validateDescription(String description) </p>
	 *
	 * <p> Description: The description is required because it holds the actual lesson.  A lesson
	 * with a title and nothing else records that something happened but not what (CS-10). </p>
	 *
	 * @param description the body the user typed
	 *
	 * @return null when the description is acceptable, otherwise the problem found
	 */
	public static Problem validateDescription(String description) {
		if (isBlank(description))
			return new Problem("The description is required",
					"Describe the question or the problem this lesson is about.");

		String lengthProblem = checkLongText(description, MAX_DESCRIPTION, "Description");
		if (!lengthProblem.isEmpty())
			return new Problem("The description is too long", lengthProblem);

		return null;
	}


	/*****
	 * <p> Method: Problem validateResolution(String resolution) </p>
	 *
	 * <p> Description: The resolution is optional, because a lesson can be recorded before anyone
	 * has resolved it, in the same way an Ed Discussion question can sit for a while before it is
	 * answered.  When it is left blank it is accepted; when it is filled in it still has to be
	 * within the length limit (CS-11). </p>
	 *
	 * @param resolution the reply the user typed, which may be empty
	 *
	 * @return null when the resolution is acceptable, otherwise the problem found
	 */
	public static Problem validateResolution(String resolution) {
		if (isBlank(resolution))
			return null;

		String lengthProblem = checkLongText(resolution, MAX_RESOLUTION, "Resolution");
		if (!lengthProblem.isEmpty())
			return new Problem("The resolution is too long", lengthProblem);

		return null;
	}


	/*****
	 * <p> Method: Problem validateContributor(String contributor) </p>
	 *
	 * <p> Description: The contributor is required because a lesson with no author cannot be
	 * credited, and the ownership rule (CS-9) has nothing to compare against.  In TP2 the screens
	 * fill this in from the logged in account rather than letting the user type it, so this check
	 * guards against a programming error rather than a typing error. </p>
	 *
	 * @param contributor the username of the contributor
	 *
	 * @return null when the contributor is acceptable, otherwise the problem found
	 */
	public static Problem validateContributor(String contributor) {
		if (isBlank(contributor))
			return new Problem("The contributor is required",
					"A lesson must record the username of the person contributing it.");

		String lengthProblem = InputLimits.checkLength(contributor, MAX_CONTRIBUTOR, "Contributor");
		if (!lengthProblem.isEmpty())
			return new Problem("The contributor is too long", lengthProblem);

		return null;
	}


	/*****
	 * <p> Method: String checkLongText(String input, int maxLength, String fieldName) </p>
	 *
	 * <p> Description: The length check for the two long text fields.  It works the same way as
	 * InputLimits.checkLength, a cheap ceiling first and then the field's own limit, but with a
	 * ceiling large enough for a real lesson.  See LONG_TEXT_ABSOLUTE_MAX for why this exists. </p>
	 *
	 * @param input     the text the user typed; null is treated as empty
	 * @param maxLength the limit for this field
	 * @param fieldName the name shown to the user in the message
	 *
	 * @return an empty string when the length is acceptable, otherwise the message to show
	 */
	public static String checkLongText(String input, int maxLength, String fieldName) {
		if (input == null)
			return "";

		if (input.length() > LONG_TEXT_ABSOLUTE_MAX)
			return fieldName + " is far too long. The limit is " + maxLength + " characters.";

		if (input.length() > maxLength)
			return fieldName + " must be " + maxLength + " characters or fewer. You entered "
					+ input.length() + ".";

		return "";
	}


	/*****
	 * <p> Method: boolean isBlank(String input) </p>
	 *
	 * <p> Description: True when the input is null, empty, or only spaces.  The HW2 version used
	 * InputLimits.isEmpty, which accepted a title of three spaces; that would appear in the list
	 * as a blank line, which is the exact problem the required field rule exists to prevent. </p>
	 *
	 * @param input the text to examine
	 *
	 * @return true when there is nothing but white space in the input
	 */
	public static boolean isBlank(String input) {
		return input == null || input.trim().isEmpty();
	}
}
