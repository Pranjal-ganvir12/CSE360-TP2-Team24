package testingAutomation;

/*******
 * <p> Title: TP2TestRunner Class. </p>
 *
 * <p> Description: Runs every semi-automated TP2 Contributor test suite in one go and prints a
 * combined summary, so the whole set of checks can be demonstrated from a single starting
 * point in the TestingScreencast.  The suites are: </p>
 * <ul>
 *   <li>{@link LessonValidationTestingAutomation}: 25 tests, VAL-01 to VAL-25, for CS-10 and
 *       CS-11 (required fields and length limits)</li>
 *   <li>{@link LessonsCrudTestingAutomation}: 14 tests, CRUD-01 to CRUD-14, for CS-1, CS-3,
 *       CS-7, CS-8, CS-9, and CS-12 (including close-and-reopen persistence)</li>
 *   <li>{@link LessonsListTestingAutomation}: 13 tests, LIST-01 to LIST-13, for CS-2, CS-4,
 *       CS-5, and CS-6</li>
 *   <li>{@link LessonsModelTestingAutomation}: 24 tests, MOD-01 to MOD-24, for CS-1, CS-2,
 *       CS-4 to CS-9, and CS-11 at the GUI Model layer</li>
 * </ul>
 *
 * <p> The manual procedures, MAN-01 to MAN-07, need a person at the running application, so
 * they are not run from here; run {@link LessonsManualTestProcedures} separately.  The TP1
 * runner is unchanged and can still be run on its own from {@link TP1TestRunner}. </p>
 *
 * <p> How to read the output: each suite prints one Pass or Failure line per test and its own
 * totals; the combined block at the end adds them up.  "RESULT: every test behaved as
 * expected" is printed only when no suite reported a failure. </p>
 *
 * <p> Copyright: CSE 360 TP2 Team: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir,
 * Moksha Dev. </p>
 *
 * @author CSE 360 TP2 Team
 *
 * @version 1.00  2026-10-22  Initial version for Team Project Phase 2
 * @version 1.20  2026-10-26  76 tests in four suites (MOD-21 to MOD-24 added)
 * @version 1.10  2026-10-25  72 tests in four suites; manual procedures listed
 */
public class TP2TestRunner {

	/**********
	 * <p> Method: main </p>
	 *
	 * <p> Description: Runs each TP2 suite in turn, then reports the combined totals.
	 * No suite uses the application's FoundationDatabase file: the suites use in-memory
	 * databases, and CRUD-11 and CRUD-12 use a file in a temporary folder that is removed
	 * afterwards. </p>
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {

		System.out.println("############################################################");
		System.out.println("  TP2 Test Runner");
		System.out.println("  Team: Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir,");
		System.out.println("        Moksha Dev");
		System.out.println("############################################################");

		LessonValidationTestingAutomation.main(args);
		LessonsCrudTestingAutomation.main(args);
		LessonsListTestingAutomation.main(args);
		LessonsModelTestingAutomation.main(args);

		int totalPassed = LessonValidationTestingAutomation.numPassed
				+ LessonsCrudTestingAutomation.numPassed
				+ LessonsListTestingAutomation.numPassed
				+ LessonsModelTestingAutomation.numPassed;

		int totalFailed = LessonValidationTestingAutomation.numFailed
				+ LessonsCrudTestingAutomation.numFailed
				+ LessonsListTestingAutomation.numFailed
				+ LessonsModelTestingAutomation.numFailed;

		System.out.println();
		System.out.println("############################################################");
		System.out.println("  Combined TP2 result");
		System.out.println("############################################################");
		System.out.println("  Validation suite   passed " + LessonValidationTestingAutomation.numPassed
				+ ", failed " + LessonValidationTestingAutomation.numFailed);
		System.out.println("  CRUD suite         passed " + LessonsCrudTestingAutomation.numPassed
				+ ", failed " + LessonsCrudTestingAutomation.numFailed);
		System.out.println("  List suite         passed " + LessonsListTestingAutomation.numPassed
				+ ", failed " + LessonsListTestingAutomation.numFailed);
		System.out.println("  Model suite        passed " + LessonsModelTestingAutomation.numPassed
				+ ", failed " + LessonsModelTestingAutomation.numFailed);
		System.out.println("  ------------------------------------------------------");
		System.out.println("  Total tests  " + (totalPassed + totalFailed));
		System.out.println("  Total passed " + totalPassed);
		System.out.println("  Total failed " + totalFailed);

		if (totalFailed == 0)
			System.out.println("\n  RESULT: every test behaved as expected.");
		else
			System.out.println("\n  RESULT: " + totalFailed
					+ " failure(s). Look above for the cases marked Failure.");
	}
}
