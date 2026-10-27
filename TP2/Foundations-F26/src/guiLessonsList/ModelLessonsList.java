package guiLessonsList;

import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: ModelLessonsList Class. </p>
 *
 * <p> Description: The Lessons Learned list page Model.  It holds the list of every stored lesson
 * and decides which of them the page shows, given the search term, the category filter, and the
 * "only my lessons" choice the user has made, and what the status line under the list says. </p>
 *
 * <p> In most of the supplied GUI packages the Model is an empty stub, because those pages hold
 * no data of their own.  This page does: it narrows a list and has to explain the result.  That
 * logic is kept here rather than in the View or the Controller for two reasons.  It contains no
 * JavaFX, so the testing automation can check exactly the decision the page makes without
 * opening a window.  And it keeps the View to building widgets and the Controller to reacting to
 * them, which is the division the supplied packages follow. </p>
 *
 * <p> Contributor stories supported: CS-2 (see all lessons, and be told in words when there are
 * none), CS-4 (search), CS-5 (filter by category), CS-6 (only my lessons). </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-16  Initial version for Team Project Phase 2
 */
public class ModelLessonsList {

	/** The text shown in the category filter for "do not filter by category".  It is a constant
	 *  so the View that offers it and the Model that recognises it cannot spell it differently,
	 *  and it is taken from LessonInputPolicy so validation refuses exactly this name as a real
	 *  category. */
	public static final String ALL_CATEGORIES = validation.LessonInputPolicy.RESERVED_CATEGORY;

	/** Source: CS-2.  Every stored lesson, newest first, read when the page is displayed.  The
	 *  lesson editor and the lesson detail page use this same list, so a change made on either of
	 *  them is already in the list when the user comes back, without reading every row again. */
	private static LessonsLearnedList allLessons = new LessonsLearnedList();

	/** Source: CS-2 to CS-6.  The subset currently on screen, in the order shown, so the row the
	 *  user selects can be turned back into the lesson it describes. */
	private static LessonsLearnedList shownLessons = new LessonsLearnedList();


	/**
	 * Default constructor is not used.  Every member of this class is static, matching the
	 * singleton use of the supplied MVC classes.
	 */
	public ModelLessonsList() {
	}


	/*****
	 * <p> Method: void setAllLessons(LessonsLearnedList lessons) </p>
	 *
	 * <p> Description: Replaces the list of every lesson, normally with a fresh read from the
	 * database when the page is displayed. </p>
	 *
	 * @param lessons the full list; a null is treated as an empty list
	 */
	public static void setAllLessons(LessonsLearnedList lessons) {
		allLessons = (lessons == null) ? new LessonsLearnedList() : lessons;
		shownLessons = allLessons;
	}


	/** @return the list of every lesson, shared with the editor and detail pages */
	public static LessonsLearnedList getAllLessons() { return allLessons; }

	/** @return the subset of lessons currently shown, in display order */
	public static LessonsLearnedList getShownLessons() { return shownLessons; }


	/*****
	 * <p> Method: LessonsLearnedList applyFilters(LessonsLearnedList all, String searchTerm,
	 *      String category, boolean onlyMine, String userName) </p>
	 *
	 * <p> Description: Narrows the full list in three independent steps.  Each step is skipped
	 * when the user has not asked for it, so with no search term, all categories, and the box
	 * unticked, the whole list is shown (CS-2).  An empty search term therefore means "do not
	 * search" on this page rather than "match nothing", which is what LessonsLearnedList.
	 * searchByText returns for it; the page has a separate Show All button, so the user is never
	 * left looking at an empty list because the search box happens to be empty. </p>
	 *
	 * <p> The steps are applied search first, then category, then ownership, but the result is
	 * the same in any order, because each step only removes lessons. </p>
	 *
	 * @param all        the full list
	 * @param searchTerm the text in the search box, which may be empty
	 * @param category   the category chosen, or ALL_CATEGORIES
	 * @param onlyMine   true when only the user's own lessons should be shown
	 * @param userName   the logged in username
	 *
	 * @return the subset to show, possibly empty
	 */
	public static LessonsLearnedList applyFilters(LessonsLearnedList all, String searchTerm,
			String category, boolean onlyMine, String userName) {
		LessonsLearnedList result = (all == null) ? new LessonsLearnedList() : all;

		if (searchTerm != null && !searchTerm.trim().isEmpty())
			result = result.searchByText(searchTerm);

		if (category != null && !category.trim().isEmpty() && !category.equals(ALL_CATEGORIES))
			result = result.filterByCategory(category);

		if (onlyMine)
			result = result.filterByContributor(userName);

		return result;
	}


	/*****
	 * <p> Method: LessonsLearnedList refreshShownLessons(String searchTerm, String category,
	 *      boolean onlyMine, String userName) </p>
	 *
	 * <p> Description: Applies the filters to the shared full list and remembers the result as the
	 * lessons on screen. </p>
	 *
	 * @param searchTerm the text in the search box, which may be empty
	 * @param category   the category chosen, or ALL_CATEGORIES
	 * @param onlyMine   true when only the user's own lessons should be shown
	 * @param userName   the logged in username
	 *
	 * @return the subset now shown
	 */
	public static LessonsLearnedList refreshShownLessons(String searchTerm, String category,
			boolean onlyMine, String userName) {
		shownLessons = applyFilters(allLessons, searchTerm, category, onlyMine, userName);
		return shownLessons;
	}


	/*****
	 * <p> Method: String statusMessage(int shown, int total, String searchTerm, String category,
	 *      boolean onlyMine) </p>
	 *
	 * <p> Description: The line under the list that says what the user is looking at.  CS-2 and
	 * CS-4 both require that an empty result is explained in words rather than shown as a blank
	 * list, because a blank list looks the same as a page that did not work.  The message names
	 * the filters that produced an empty result, so the user knows which one to change. </p>
	 *
	 * @param shown      how many lessons are shown
	 * @param total      how many lessons are stored
	 * @param searchTerm the search term in use, which may be empty
	 * @param category   the category in use, or ALL_CATEGORIES
	 * @param onlyMine   whether only the user's own lessons are shown
	 *
	 * @return the message to display
	 */
	public static String statusMessage(int shown, int total, String searchTerm, String category,
			boolean onlyMine) {
		if (total == 0)
			return "No lessons have been recorded yet. Press New Lesson to record the first one.";

		if (shown > 0)
			return "Showing " + shown + " of " + total + (total == 1 ? " lesson." : " lessons.");

		boolean searching = searchTerm != null && !searchTerm.trim().isEmpty();
		boolean filtering = category != null && !category.trim().isEmpty()
				&& !category.equals(ALL_CATEGORIES);

		// Only the "my lessons" box is ticked: the honest explanation is that this user has not
		// written anything yet, not that something failed to match.
		if (!searching && !filtering && onlyMine)
			return "You have not contributed any lessons yet. Press New Lesson to record one.";

		StringBuilder why = new StringBuilder("No");
		if (onlyMine)
			why.append(" lessons of yours");
		else
			why.append(" lessons");
		if (searching)
			why.append(" match \"").append(searchTerm.trim()).append("\"");
		if (filtering)
			why.append(" in the category ").append(category.trim());
		why.append(". Press Show All to see every lesson.");
		return why.toString();
	}


	/*****
	 * <p> Method: LessonsLearned lessonAt(int index) </p>
	 *
	 * <p> Description: Turns the row the user selected into the lesson it describes (CS-3). </p>
	 *
	 * @param index the selected row, starting at zero; -1 means nothing is selected
	 *
	 * @return the lesson on that row, or null when nothing is selected
	 */
	public static LessonsLearned lessonAt(int index) {
		return shownLessons.get(index);
	}
}
