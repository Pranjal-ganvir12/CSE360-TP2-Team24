package testingAutomation;

import java.sql.SQLException;

import database.LessonsLearnedDatabase;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import guiLessonDetail.ModelLessonDetail;
import guiLessonEditor.ModelLessonEditor;
import guiLessonsList.ModelLessonsList;
import validation.LessonInputPolicy.Problem;

/*******
 * <p> Title: LessonsModelTestingAutomation Class. </p>
 *
 * <p> Description: Semi-automated tests for the Models of the three Contributor GUI packages:
 * {@link guiLessonsList.ModelLessonsList}, {@link guiLessonEditor.ModelLessonEditor}, and
 * {@link guiLessonDetail.ModelLessonDetail}.  Each Controller passes what the user typed or
 * selected to its Model and displays what the Model returns, so these tests check the same
 * decisions the windows make, without opening a window.  What only a window can show (the
 * confirmation dialog, disabled buttons, the status line on screen) is covered by the manual
 * procedures in {@link LessonsManualTestProcedures}. </p>
 *
 * <p> Requirements covered: CS-1 (create, contributor set from the logged in user), CS-2 (the
 * list and its empty-state message), CS-4 (search and its no-match message), CS-5 (category
 * filter and its no-match message), CS-6 (only my lessons), CS-7 (update), CS-8 (delete), and
 * CS-9 (only the contributor may update or delete). </p>
 *
 * <p> The tests share one in-memory database and run in id order.  MOD-01 stores a lesson for
 * arushugal and a second lesson is stored for karanpatel before MOD-03; later tests use these
 * two lessons. </p>
 *
 * <p> Each test is one private method named after its id, whose Javadoc states the
 * requirement, how the test exercises the code, and the condition for a Pass. </p>
 *
 * <p> How to read the output: one "Pass:" or "Failure:" line per test id, then the suite
 * totals. </p>
 *
 * <p> Copyright: Lynn Robert Carter (c) 2025.  TP2 additions by CSE 360 Team 24: Arush Ugal,
 * Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-21  Initial version for Team Project Phase 2
 * @version 1.10  2026-10-25  One method per test; status message tests for CS-2, CS-4, CS-5;
 *                            non-owner edit test
 */
public class LessonsModelTestingAutomation {

	/** Count of tests that produced the expected result in this suite. */
	public static int numPassed = 0;

	/** Count of tests that did not produce the expected result in this suite. */
	public static int numFailed = 0;

	private static final String ALL = ModelLessonsList.ALL_CATEGORIES;
	private static final String H2_TITLE = "H2 file lock prevents a second connection";
	private static final String FX_TITLE = "JavaFX Application Thread rule";

	// The open in-memory database shared by every test.
	private static LessonsLearnedDatabase db;


	/**
	 * Default constructor is not used.  Every member of this class is static.
	 */
	public LessonsModelTestingAutomation() {
	}


	/**********
	 * <p> Method: main </p>
	 *
	 * <p> Description: Opens the in-memory database, loads it into ModelLessonsList the way the
	 * list page does when it is displayed, and runs every Model test in id order. </p>
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		numPassed = 0;
		numFailed = 0;

		System.out.println("------------------------------------------------------------");
		System.out.println("  Lessons GUI Model Tests (CS-1, CS-2, CS-4 to CS-9)");
		System.out.println("------------------------------------------------------------");

		db = new LessonsLearnedDatabase();
		try {
			db.connectToDatabase("jdbc:h2:mem:tp2modeltest;DB_CLOSE_DELAY=-1");
			db.deleteAllLessons();
			reload();

			mod01SaveNewLesson();
			mod02SaveNewLessonBlankTitle();
			ModelLessonEditor.saveNewLesson(db, ModelLessonsList.getAllLessons(), "karanpatel",
					FX_TITLE, "GUI", "JavaFX, threading",
					"All widget updates must happen on the Application Thread.",
					"Wrap background calls in Platform.runLater.");
			reload();
			mod03NoFiltersShowsAll();
			mod04MessageWhenUserHasNoLessons();
			mod05Search();
			mod06SearchForAbsentTerm();
			mod07CategoryFilter();
			mod08AbsentCategory();
			mod09OnlyMine();
			mod10OnlyMineForUserWithNone();
			mod11OwnerMayChange();
			mod12NonOwnerMayNotChange();
			mod13OwnerUpdate();
			mod14UpdateBlankDescription();
			mod15OwnerDelete();
			mod16NonOwnerDelete();
			mod17MessageForNoSearchMatch();
			mod18MessageForNoCategoryMatch();
			mod19MessageWhenNoLessonsStored();
			mod20NonOwnerEdit();
			mod21LeadingEmptyKeywordRefused();
			mod22TrailingEmptyKeywordRefused();
			mod23ReservedCategoryRefused();
		} catch (SQLException e) {
			System.out.println("  SETUP FAILED: " + e.getMessage());
			numFailed += 23;
		} finally {
			db.closeConnection();
		}

		System.out.println("  -> passed " + numPassed + ", failed " + numFailed);
	}

	// Reads every lesson into ModelLessonsList and shows them all, as displaying the list page
	// does.  Called after each change so the tests see what the page would see.
	private static void reload() {
		ModelLessonsList.setAllLessons(LessonsLearnedList.readAll(db));
		ModelLessonsList.refreshShownLessons("", ALL, false, "");
	}

	// The stored lesson with the given title, read from the shared list.
	private static LessonsLearned lessonTitled(String title) {
		for (LessonsLearned lesson : ModelLessonsList.getAllLessons().getAll())
			if (title.equals(lesson.getTitle()))
				return lesson;
		return null;
	}


	/*-*******************************************************************************************
	 *
	 * CS-1 Create, through the lesson editor
	 *
	 */

	/**
	 * <p> MOD-01 (CS-1, positive).  The editor stores a new lesson and records the logged in
	 * user as its contributor. </p>
	 * <p> How: ModelLessonEditor.saveNewLesson is called with "arushugal" as the logged in
	 * user, which is how ControllerLessonEditor calls it.  The editor has no contributor field,
	 * so this is the only way the contributor can be set. </p>
	 * <p> Pass when: saveNewLesson returns null, the stored list grows by one, and the stored
	 * lesson's contributor is "arushugal". </p>
	 */
	private static void mod01SaveNewLesson() {
		int before = ModelLessonsList.getAllLessons().size();
		Problem problem = ModelLessonEditor.saveNewLesson(db, ModelLessonsList.getAllLessons(),
				"arushugal", H2_TITLE, "Database", "H2, JDBC",
				"The H2 engine locks the file to one JVM process at a time.",
				"Use AUTO_SERVER=TRUE or an in-memory URL for tests.");
		reload();
		LessonsLearned saved = lessonTitled(H2_TITLE);
		check("MOD-01 editor stores lesson with logged-in user as contributor (positive, CS-1)",
				problem == null && ModelLessonsList.getAllLessons().size() == before + 1
				&& saved != null && "arushugal".equals(saved.getContributor()));
	}

	/**
	 * <p> MOD-02 (CS-1, negative).  The editor refuses a lesson with a blank title and stores
	 * nothing. </p>
	 * <p> How: saveNewLesson is called with a title of "". </p>
	 * <p> Pass when: the problem is "The title is required" and the stored list is unchanged. </p>
	 */
	private static void mod02SaveNewLessonBlankTitle() {
		int before = ModelLessonsList.getAllLessons().size();
		Problem problem = ModelLessonEditor.saveNewLesson(db, ModelLessonsList.getAllLessons(),
				"arushugal", "", "Database", "", "Some description here.", "");
		reload();
		check("MOD-02 editor refuses blank title, nothing stored (negative, CS-1)",
				problem != null && "The title is required".equals(problem.title())
				&& ModelLessonsList.getAllLessons().size() == before);
	}


	/*-*******************************************************************************************
	 *
	 * CS-2 The list and its messages
	 *
	 */

	/**
	 * <p> MOD-03 (CS-2, positive).  With no search term, all categories, and "only mine"
	 * unticked, the page shows every lesson.  An empty search box means "no search" on the page. </p>
	 * <p> How: refreshShownLessons("", ALL_CATEGORIES, false, "arushugal"). </p>
	 * <p> Pass when: both stored lessons are shown and the status line reads
	 * "Showing 2 of 2 lessons.". </p>
	 */
	private static void mod03NoFiltersShowsAll() {
		ModelLessonsList.refreshShownLessons("", ALL, false, "arushugal");
		check("MOD-03 no filters shows every lesson, 'Showing 2 of 2' (positive, CS-2)",
				ModelLessonsList.getShownLessons().size() == 2
				&& "Showing 2 of 2 lessons.".equals(
						ModelLessonsList.statusMessage(2, 2, "", ALL, false)));
	}

	/**
	 * <p> MOD-19 (CS-2, negative).  When no lesson is stored at all, the status line says so
	 * in words instead of leaving a blank list. </p>
	 * <p> How: statusMessage(0, 0, "", ALL_CATEGORIES, false). </p>
	 * <p> Pass when: the message is "No lessons have been recorded yet. Press New Lesson to
	 * record the first one.". </p>
	 */
	private static void mod19MessageWhenNoLessonsStored() {
		check("MOD-19 empty database explained in words (negative, CS-2)",
				("No lessons have been recorded yet. Press New Lesson to record the first one.")
				.equals(ModelLessonsList.statusMessage(0, 0, "", ALL, false)));
	}


	/*-*******************************************************************************************
	 *
	 * CS-4 Search
	 *
	 */

	/**
	 * <p> MOD-05 (CS-4, positive).  A search on the page shows the matching lesson. </p>
	 * <p> How: refreshShownLessons("JDBC", ...).  Only the H2 lesson has JDBC as a keyword. </p>
	 * <p> Pass when: exactly one lesson is shown and it is the H2 lesson. </p>
	 */
	private static void mod05Search() {
		ModelLessonsList.refreshShownLessons("JDBC", ALL, false, "arushugal");
		check("MOD-05 search 'JDBC' shows only the H2 lesson (positive, CS-4)",
				ModelLessonsList.getShownLessons().size() == 1
				&& H2_TITLE.equals(ModelLessonsList.lessonAt(0).getTitle()));
	}

	/**
	 * <p> MOD-06 (CS-4, negative).  A search that matches nothing shows no lessons. </p>
	 * <p> How: refreshShownLessons("xyzzy", ...). </p>
	 * <p> Pass when: no lesson is shown. </p>
	 */
	private static void mod06SearchForAbsentTerm() {
		ModelLessonsList.refreshShownLessons("xyzzy", ALL, false, "arushugal");
		check("MOD-06 search 'xyzzy' shows no lessons (negative, CS-4)",
				ModelLessonsList.getShownLessons().isEmpty());
	}

	/**
	 * <p> MOD-17 (CS-4, negative).  A search that matches nothing is explained by a message
	 * that names the search term. </p>
	 * <p> How: statusMessage(0, 2, "xyzzy", ALL_CATEGORIES, false). </p>
	 * <p> Pass when: the message is "No lessons match "xyzzy". Press Show All to see every
	 * lesson.". </p>
	 */
	private static void mod17MessageForNoSearchMatch() {
		check("MOD-17 no-match search explained, naming the term (negative, CS-4)",
				"No lessons match \"xyzzy\". Press Show All to see every lesson.".equals(
						ModelLessonsList.statusMessage(0, 2, "xyzzy", ALL, false)));
	}


	/*-*******************************************************************************************
	 *
	 * CS-5 Filter by category
	 *
	 */

	/**
	 * <p> MOD-07 (CS-5, positive).  Choosing a category shows only that category. </p>
	 * <p> How: refreshShownLessons("", "GUI", ...). </p>
	 * <p> Pass when: exactly one lesson is shown and it is the JavaFX lesson. </p>
	 */
	private static void mod07CategoryFilter() {
		ModelLessonsList.refreshShownLessons("", "GUI", false, "arushugal");
		check("MOD-07 category 'GUI' shows only the JavaFX lesson (positive, CS-5)",
				ModelLessonsList.getShownLessons().size() == 1
				&& FX_TITLE.equals(ModelLessonsList.lessonAt(0).getTitle()));
	}

	/**
	 * <p> MOD-08 (CS-5, negative).  A category with no lessons shows none. </p>
	 * <p> How: refreshShownLessons("", "Cryptography", ...). </p>
	 * <p> Pass when: no lesson is shown. </p>
	 */
	private static void mod08AbsentCategory() {
		ModelLessonsList.refreshShownLessons("", "Cryptography", false, "arushugal");
		check("MOD-08 category 'Cryptography' shows no lessons (negative, CS-5)",
				ModelLessonsList.getShownLessons().isEmpty());
	}

	/**
	 * <p> MOD-18 (CS-5, negative).  A category with no lessons is explained by a message that
	 * names the category. </p>
	 * <p> How: statusMessage(0, 2, "", "Cryptography", false). </p>
	 * <p> Pass when: the message is "No lessons in the category Cryptography. Press Show All
	 * to see every lesson.". </p>
	 */
	private static void mod18MessageForNoCategoryMatch() {
		check("MOD-18 empty category explained, naming the category (negative, CS-5)",
				"No lessons in the category Cryptography. Press Show All to see every lesson."
				.equals(ModelLessonsList.statusMessage(0, 2, "", "Cryptography", false)));
	}


	/*-*******************************************************************************************
	 *
	 * CS-6 Only my lessons
	 *
	 */

	/**
	 * <p> MOD-09 (CS-6, positive).  Ticking "only mine" shows the user's own lessons. </p>
	 * <p> How: refreshShownLessons("", ALL_CATEGORIES, true, "arushugal"). </p>
	 * <p> Pass when: exactly one lesson is shown and its contributor is arushugal. </p>
	 */
	private static void mod09OnlyMine() {
		ModelLessonsList.refreshShownLessons("", ALL, true, "arushugal");
		check("MOD-09 'only mine' for arushugal shows only arushugal's lesson (positive, CS-6)",
				ModelLessonsList.getShownLessons().size() == 1
				&& "arushugal".equals(ModelLessonsList.lessonAt(0).getContributor()));
	}

	/**
	 * <p> MOD-10 (CS-6, negative).  "Only mine" for a user who has contributed nothing shows
	 * no lessons. </p>
	 * <p> How: refreshShownLessons("", ALL_CATEGORIES, true, "nobody"). </p>
	 * <p> Pass when: no lesson is shown. </p>
	 */
	private static void mod10OnlyMineForUserWithNone() {
		ModelLessonsList.refreshShownLessons("", ALL, true, "nobody");
		check("MOD-10 'only mine' for a user with no lessons shows none (negative, CS-6)",
				ModelLessonsList.getShownLessons().isEmpty());
	}

	/**
	 * <p> MOD-04 (CS-6, negative).  When "only mine" is the only filter and the user has no
	 * lessons, the message says the user has not contributed any, rather than that nothing
	 * matched. </p>
	 * <p> How: statusMessage(0, 2, "", ALL_CATEGORIES, true). </p>
	 * <p> Pass when: the message is "You have not contributed any lessons yet. Press New
	 * Lesson to record one.". </p>
	 */
	private static void mod04MessageWhenUserHasNoLessons() {
		check("MOD-04 user with no lessons told so in words (negative, CS-6)",
				"You have not contributed any lessons yet. Press New Lesson to record one."
				.equals(ModelLessonsList.statusMessage(0, 2, "", ALL, true)));
	}


	/*-*******************************************************************************************
	 *
	 * CS-9 Only the contributor may change a lesson
	 *
	 */

	/**
	 * <p> MOD-11 (CS-9, positive).  The detail page lets the contributor edit and delete. </p>
	 * <p> How: ModelLessonDetail.canChange(H2 lesson, "arushugal"), the call ViewLessonDetail
	 * uses to enable the Edit and Delete buttons. </p>
	 * <p> Pass when: canChange returns true and the ownership note says the user can edit or
	 * delete. </p>
	 */
	private static void mod11OwnerMayChange() {
		LessonsLearned h2 = lessonTitled(H2_TITLE);
		check("MOD-11 contributor may edit and delete (positive, CS-9)",
				ModelLessonDetail.canChange(h2, "arushugal")
				&& ModelLessonDetail.ownershipNote(h2, "arushugal").startsWith("You contributed"));
	}

	/**
	 * <p> MOD-12 (CS-9, negative).  The detail page does not let anyone else edit or delete,
	 * and says whose lesson it is. </p>
	 * <p> How: canChange(H2 lesson, "karanpatel"). </p>
	 * <p> Pass when: canChange returns false and the ownership note names arushugal as the
	 * contributor. </p>
	 */
	private static void mod12NonOwnerMayNotChange() {
		LessonsLearned h2 = lessonTitled(H2_TITLE);
		check("MOD-12 other users may not edit or delete, note names owner (negative, CS-9)",
				!ModelLessonDetail.canChange(h2, "karanpatel")
				&& ModelLessonDetail.ownershipNote(h2, "karanpatel").contains("arushugal"));
	}

	/**
	 * <p> MOD-20 (CS-9, negative).  The editor refuses changes from anyone but the contributor,
	 * even though the page never offers them Edit, and the stored lesson is not changed. </p>
	 * <p> How: ModelLessonEditor.saveChanges is called on karanpatel's JavaFX lesson with
	 * "arushugal" as the logged in user and a new title. </p>
	 * <p> Pass when: the problem is "Only the contributor can change this lesson" and the
	 * stored title is unchanged. </p>
	 */
	private static void mod20NonOwnerEdit() {
		LessonsLearned fx = lessonTitled(FX_TITLE);
		Problem problem = ModelLessonEditor.saveChanges(db, ModelLessonsList.getAllLessons(), fx,
				"arushugal", "Changed by someone else", fx.getCategory(), fx.getKeywords(),
				fx.getDescription(), fx.getResolution());
		LessonsLearned stored = LessonsLearned.read(db, fx.getId());
		check("MOD-20 editor refuses a non-contributor's changes (negative, CS-9)",
				problem != null
				&& "Only the contributor can change this lesson".equals(problem.title())
				&& stored != null && FX_TITLE.equals(stored.getTitle()));
	}

	/**
	 * <p> MOD-21 (CS-10, negative).  The editor preserves a leading empty keyword so validation
	 * refuses it instead of silently changing what the contributor typed. </p>
	 */
	private static void mod21LeadingEmptyKeywordRefused() {
		String tidied = ModelLessonEditor.tidyKeywords(", java");
		Problem problem = validation.LessonInputPolicy.validateKeywords(tidied);
		check("MOD-21 leading empty keyword is preserved and refused (negative, CS-10)",
				", java".equals(tidied) && problem != null
				&& "A keyword is empty".equals(problem.title()));
	}

	/**
	 * <p> MOD-22 (CS-10, negative).  A trailing comma is an empty keyword and is refused. </p>
	 */
	private static void mod22TrailingEmptyKeywordRefused() {
		String tidied = ModelLessonEditor.tidyKeywords("java,");
		Problem problem = validation.LessonInputPolicy.validateKeywords(tidied);
		check("MOD-22 trailing empty keyword is refused (negative, CS-10)",
				"java, ".equals(tidied) && problem != null
				&& "A keyword is empty".equals(problem.title()));
	}

	/**
	 * <p> MOD-23 (CS-5, negative).  The editor refuses the list screen's sentinel label as a
	 * real category, because such a lesson could not be filtered by that category. </p>
	 */
	private static void mod23ReservedCategoryRefused() {
		Problem problem = ModelLessonEditor.saveNewLesson(db, ModelLessonsList.getAllLessons(),
				"mokshadev", "Reserved category check", ModelLessonsList.ALL_CATEGORIES,
				"filter", "This should not be stored under the filter sentinel.", "");
		check("MOD-23 reserved category name is refused (negative, CS-5)",
				problem != null && "The category name is reserved".equals(problem.title())
				&& lessonTitled("Reserved category check") == null);
	}


	/*-*******************************************************************************************
	 *
	 * CS-7 Update, through the lesson editor
	 *
	 */

	/**
	 * <p> MOD-13 (CS-7, positive).  The contributor's change to the title is stored. </p>
	 * <p> How: saveChanges is called on the H2 lesson as "arushugal" with a new title; the
	 * lesson is then read again from the database. </p>
	 * <p> Pass when: saveChanges returns null and the stored title is the new one. </p>
	 */
	private static void mod13OwnerUpdate() {
		LessonsLearned h2 = lessonTitled(H2_TITLE);
		Problem problem = ModelLessonEditor.saveChanges(db, ModelLessonsList.getAllLessons(), h2,
				"arushugal", "H2 file lock: one process at a time", h2.getCategory(),
				h2.getKeywords(), h2.getDescription(), h2.getResolution());
		LessonsLearned stored = LessonsLearned.read(db, h2.getId());
		check("MOD-13 contributor's title change is stored (positive, CS-7)",
				problem == null && stored != null
				&& "H2 file lock: one process at a time".equals(stored.getTitle()));
	}

	/**
	 * <p> MOD-14 (CS-7, negative).  A change that empties the description is refused, because
	 * an edit is validated the same way as a new lesson. </p>
	 * <p> How: saveChanges is called on the updated H2 lesson as "arushugal" with a description
	 * of "". </p>
	 * <p> Pass when: the problem is "The description is required" and the stored description is
	 * unchanged. </p>
	 */
	private static void mod14UpdateBlankDescription() {
		LessonsLearned h2 = lessonTitled("H2 file lock: one process at a time");
		Problem problem = ModelLessonEditor.saveChanges(db, ModelLessonsList.getAllLessons(), h2,
				"arushugal", h2.getTitle(), h2.getCategory(), h2.getKeywords(), "",
				h2.getResolution());
		LessonsLearned stored = LessonsLearned.read(db, h2.getId());
		check("MOD-14 edit with blank description refused, lesson unchanged (negative, CS-7)",
				problem != null && "The description is required".equals(problem.title())
				&& stored != null && h2.getDescription().equals(stored.getDescription()));
	}


	/*-*******************************************************************************************
	 *
	 * CS-8 Delete, through the lesson detail page
	 *
	 */

	/**
	 * <p> MOD-15 (CS-8, positive).  The contributor deletes the lesson, and it is gone from both
	 * the database and the shared list every page reads. </p>
	 * <p> How: ModelLessonDetail.deleteLesson is called on the H2 lesson as "arushugal".  The
	 * Controller asks for confirmation first; that dialog is checked by MAN-04. </p>
	 * <p> Pass when: deleteLesson returns null, reading the id returns null, and the shared
	 * list no longer holds it. </p>
	 */
	private static void mod15OwnerDelete() {
		LessonsLearned h2 = lessonTitled("H2 file lock: one process at a time");
		int id = h2.getId();
		Problem problem = ModelLessonDetail.deleteLesson(db, ModelLessonsList.getAllLessons(), h2,
				"arushugal");
		check("MOD-15 contributor deletes lesson; gone from database and list (positive, CS-8)",
				problem == null && LessonsLearned.read(db, id) == null
				&& ModelLessonsList.getAllLessons().findById(id) == null);
		reload();
	}

	/**
	 * <p> MOD-16 (CS-8 and CS-9, negative).  A delete by someone other than the contributor is
	 * refused and the lesson stays stored. </p>
	 * <p> How: deleteLesson is called on karanpatel's JavaFX lesson as "arushugal". </p>
	 * <p> Pass when: the problem is "Only the contributor can delete this lesson" and the lesson
	 * can still be read. </p>
	 */
	private static void mod16NonOwnerDelete() {
		LessonsLearned fx = lessonTitled(FX_TITLE);
		Problem problem = ModelLessonDetail.deleteLesson(db, ModelLessonsList.getAllLessons(), fx,
				"arushugal");
		check("MOD-16 non-contributor delete refused, lesson kept (negative, CS-8, CS-9)",
				problem != null
				&& "Only the contributor can delete this lesson".equals(problem.title())
				&& LessonsLearned.read(db, fx.getId()) != null);
	}


	/*-*******************************************************************************************
	 *
	 * Helpers
	 *
	 */

	/**
	 * <p> Method: check </p>
	 *
	 * <p> Description: Records one test outcome.  Prints "Pass:" or "Failure:" followed by the
	 * label, and updates {@link #numPassed} or {@link #numFailed}. </p>
	 *
	 * @param label  the test id, what was checked, and the requirement
	 * @param result true when the behaviour matched the requirement
	 */
	private static void check(String label, boolean result) {
		if (result) {
			System.out.println("  Pass:    " + label);
			numPassed++;
		} else {
			System.out.println("  Failure: " + label);
			numFailed++;
		}
	}
}
