# TP2 Session Notes — 2026-10-25

Record of the review-and-fix session for the Team 24 TP2 submission, in the order it happened.
Everything below was checked on this machine unless marked otherwise.

---

## 1. Review of the original `TP2_MASTER.zip`

Input: `~/Downloads/TP2_MASTER.zip` (Arush's organised master folder: `SUBMIT_THIS/TP2`,
`SCRIPTS_AND_WORKING`, `STALE_FILES`, helper notes). Reviewed against the TP2 assignment text
and rubric.

**Verified working:** source compiled (67 files, 0 errors); `TP2TestRunner` 63/63; no broken
Javadoc links.

**Findings, by severity**

Critical
- No screencasts at all (9 needed: 3 main + 6 Status Summary). Task 9 is 35%.
- Deadline mismatch: the assignment says "Due Monday"; the README said "Monday, October 27",
  but Oct 27, 2026 is a Tuesday. The real deadline is most likely **Mon Oct 26, 11:59 pm**.
- **Functional bug:** HW2 and TP2 share the table name `lessonsLearned` in
  `~/FoundationDatabase`. An HW2 table (author/body/tags/createdAt, as in the local database)
  made every TP2 save and read fail with `Column "DESCRIPTION" not found`; the list silently
  showed 0 lessons. Reproduced on a copy of the real database. Tests missed it because they
  used in-memory databases.
- All six Plan and Status PDFs had been generated within 4 seconds on Oct 25 (PDF metadata).

Task 3 (Lessons_Learned_Javadocs)
- Javadoc generated without `-private`: the 12 attributes and their "Source:" notes were not in
  the HTML (0 occurrences).
- 3.1 screenshot was a rendered list, not real evidence, and mislabelled files as entityClasses.
- 3.4 "attributes" screenshot showed lines 1–50 (class header) and was out of date with the code.
- 3.5 "search+filter" screenshot showed `removeById/findById/get`, not search.
- No 3.4 screenshot for `LessonsLearnedList`.

Task 4 (Contributor_Javadocs)
- 4.1 was a rendered table, not the package view; few 4.4/4.5 screenshots.

Task 5 (AICoaching.pdf)
- Dead citation: `github.com/nicpanozzo/yake4j` returned 404; KeyBERT had no URL.
- Concept of operations contradicted the recommended tool for Title, Keywords, and Category.
- Resolution requirements contradicted each other.

Tasks 6/7 (tests)
- `7.4-CRUD-05-12.png` cited by the map but only present in `STALE_FILES`.
- Test screenshots out of date (`MOD-11-16` actually showed MOD-05..13).
- Tests were not separate methods; per-test Javadoc was one templated line.
- No manual tests although Task 6 asks for them.
- CS-12 "persistence" tests used an in-memory database and never reopened anything.
- Map/runner mismatches: VAL-15 and LIST-10 unmapped, VAL-18/MOD-04/MOD-16 mislabelled.

Task 2 (Team Norms)
- Norm 1 kept the old "Monday and Thursday" text unstruck beside the new text.
- TP1 "Why it matters" lines deleted without strike-through.
- Compared with the real TP1 norms (`TP1-fixed/TP1/TP1-Team24 Team Norms.pdf`): TP1 norm 10
  ("noon safety copy") had been silently replaced by a "respect" norm marked "No change".

Task 8 (Plan and Status)
- Timeline contradictions between PDFs and the iteration notes (bug-fix dates, tests "passing"
  before the suite existed, MOD-04 failing before the Model suite was written).

Housekeeping
- `.DS_Store` files; README zip command would nest `SUBMIT_THIS/`; `.project/.classpath` had
  been moved to `STALE_FILES`; presenting time unbalanced (Anirudh in two main screencasts).

---

## 2. Fixes made (working copy `~/Downloads/TP2_FIXED`)

Stale files were ignored. `.project` and `.classpath` were restored to `Foundations-F26`.

### Code
- `database/LessonsLearnedDatabase.java`: `moveLegacyTableAside()` renames a `lessonsLearned`
  table that has no `description` column to `lessonsLearnedHW2Legacy` (rows kept, never dropped)
  before creating the TP2 table. Verified on a copy of the real `~/FoundationDatabase`: save and
  list now work, and the legacy table is preserved.
- `entityClasses/LessonsLearned.java`: Javadoc corrected (TP3 attributes are excluded from the
  UPDATE, not "written back"); notes that per-user votes and reader comments need their own
  tables in TP3.
- `package-info.java` added for `entityClasses`, `guiLessonsList`, `guiLessonEditor`,
  `guiLessonDetail`, `testingAutomation`.
- Misplaced `@version` tags in `ControllerRole1Home` / `ControllerRole2Home` method Javadoc fixed.

### Tests (63 → 72 automated, plus 7 manual)
- Every test is its own private method with Javadoc: requirement, how it works, Pass condition.
- Negative validation tests check the exact refusal title (`refusedWith`).
- `LessonValidationTestingAutomation`: VAL-01..25 (VAL-15/18 correctly CS-10).
- `LessonsCrudTestingAutomation`: CRUD-01..14. CRUD-11/12 now close and reopen a database
  file in a temp folder. CRUD-13/14 build the HW2 table layout and check save/list work and the
  HW2 rows survive.
- `LessonsListTestingAutomation`: LIST-01..13; adds LIST-11 newest first, LIST-12 case-insensitive,
  LIST-13 empty term; subset contents checked, not only sizes.
- `LessonsModelTestingAutomation`: MOD-01..20; adds MOD-17/18/19 status messages and MOD-20
  non-owner edit refused; MOD-04 reassigned to CS-6.
- New `LessonsManualTestProcedures`: MAN-01..07 (empty-list message, contributor from login,
  named alert, delete confirmation, disabled buttons for others, deletion visible to others,
  restart keeps lessons). Prints steps, records P/F/S.
- `TP2TestRunner` Javadoc updated.
- Mutation check: three defects injected into a copy (HW2 fix removed, keywords dropped from
  search, list order reversed) produced six failures and no false passes.

### Javadoc and screenshots
- `working/build_javadoc.sh` regenerates the three folders with `-private`
  (Lessons_Learned: entity, database, policy; Contributor: GUI packages, Role1/2, guiTools,
  FoundationsMain; ContributorTests: the TP2 test classes).
- `working/screenshots/make_screenshots.py` builds 147 code screenshots with LaTeX listings
  straight from the source (real line numbers), 5 Chrome screenshots of Javadoc pages, and
  images of the real `TP2TestRunner` output. One screenshot per test method.

### PDFs (all LaTeX, sources in `working/latex/`, shared style `tp2style.sty`)
- `TP2 Team Norms.pdf`: tracked changes against the real TP1 norms; norms 11 and 12 new;
  rationales limited to what the team's earlier draft stated or the assignment requires;
  signature lines left blank for members to sign.
- `AICoaching.pdf`: five fields, one owner each (Description–Arush, Resolution–Moksha,
  Title–Karan, Keywords–Anirudh, Category–Pranjal); TikZ mockups matching the editor; concept of
  operations consistent with the tool; prioritised Must/Should/Could requirements; shared
  requirements C-1..C-6 (incl. consent, from LessonShare function 15). Tools and sources
  verified Oct 25: LanguageTool (LGPL-2.1), Apache OpenNLP (Apache-2.0), Groq free plan,
  KeyBERT (MIT), all-MiniLM-L6-v2 (Apache-2.0) via Deep Java Library (Apache-2.0).
- `Requirements to Tests Map.pdf`: generated by `make_map.py` from the test Javadoc; CS-1..12
  each with positive and negative tests, HTML pages, per-test anchor and screenshot; latest run
  72/72; iteration notes.
- Six `Plan and Status 2026-10-{12,14,16,19,22,24}.pdf`: generated by `make_plans.py` from one
  task table (assertions enforce: every In Progress task reported by its owner, every member has
  one). Removed the unverifiable "TA confirmed" line. Presenting-time plan in the Oct 24 PDF:
  LL Arush 3 / Anirudh 3; GUI Karan 4.5 / Moksha 4.5; Testing Pranjal 6 / Anirudh 4; Arush two
  summaries.
- `working/scripts/Screencast Guide.pdf` (not submitted): per screencast, the requirements and
  exact files to show.
- `working/build_all.sh` rebuilds everything.

**Team must confirm:** to make the plan timeline consistent, the Model suite is credited to
Karan and the CRUD-02/06 fix to Arush. PDF creation dates are Oct 25 (not backdated).

### Final verification
- 69 classes compile; `TP2TestRunner` 72/72; `TP1TestRunner` still passes.
- 0 broken links in all three Javadoc folders; every screenshot named in the map exists.
- `TP2.zip` built (31 MB, 512 files, top-level `TP2/`, no `.DS_Store`).

---

## 3. Review of `TP2_proposed_fixes.diff` (no changes made)

The diff targets the **original** source: it applies there (63/63 pass). Against the fixed
version the 4 code hunks apply; all 6 test hunks fail.

| Hunk | Verdict |
|---|---|
| Widen `description`/`resolution` to VARCHAR(2000) | Real gap (narrow HW2 columns refuse long saves). **Unsafe alone:** on an author/body HW2 table (the real local database) `ALTER COLUMN description` throws, so `FoundationsMain` shows "Database Is Already Being Used" and exits. Safe on top of the fixed version. |
| `tidyKeywords` `first` flag | Real bug: `",a"` was tidied to `"a"` and accepted. |
| Reserve "All categories" | Real bug: such a category could never be filtered. No tests added; existing rows not handled. |
| `split(",", -1)` in `validateKeywords` | Consistency fix for direct calls; 10 keywords + trailing comma now reports "too many" rather than "empty". No tests. |
| Test hunks | Superseded by the fixed tests; the CRUD-11 in-memory "reopen" does not simulate a restart. |

With the 4 code hunks on the fixed version: 72/72. Answer given to "is it required": **no**
(recommended, not required). If adopted: add VAL tests for the reserved category and trailing
comma, update the map, run `working/build_all.sh`.

---

## 4. GUI

The application is the JavaFX `applicationMain.FoundationsMain`. It was launched from the fixed
source with `-Duser.home=/tmp/tp2gui/home`, so it used a fresh empty database instead of the
real `~/FoundationDatabase` (which would be modified on first start by the legacy-table rename).
Path to the lesson windows: create admin, invite and create a Role1 user, log in, press
Lessons Learned.

---

## 5. Repository

Private repo: https://github.com/Pranjal-ganvir12/CSE360-TP2-Team24 (branch `main`).
Created through the GitHub API with the credential already stored in git (no `gh` CLI on this
machine). `.gitignore` excludes `TP2.zip`, build output, LaTeX intermediates, `.DS_Store`.

---

## 6. Still to do before the deadline

1. Confirm the deadline on Canvas (likely Mon Oct 26, 11:59 pm); noon safety upload.
2. Record the 9 screencasts (MP4/MOV, each under 100 MB) and add them to `TP2/` and
   `TP2/TP2 Planning and Tracking/`, then rebuild the zip.
3. Each member signs the Team Norms; the team confirms the Plan and Status content.
4. Decide on the four code hunks from the proposed diff (section 3).
5. `working/scripts/TP2 MAIN VIDEO SCRIPTS.md` and the standup scripts are out of date; use
   `Screencast Guide.pdf`.
6. Every member verifies the Canvas submission and completes the peer evaluation.

---

## 7. Pull request #1 (Moksha Dev), merged 2026-10-26

Moksha's review of the editor found the defects that `TP2_proposed_fixes.diff` hunks 2–4 had
described: `tidyKeywords` silently dropped an empty first keyword (", java" became "java" and was
accepted), `validateKeywords` used `split(",")` and so accepted "java," when called directly, and
"All categories" (the filter's own label) could be stored as a category and then never shown on
its own. PR #1 fixed all three and added MOD-21 to MOD-23; it did not include the unsafe
column-widening hunk.

Reviewed before merging (75/75, TP1 runner passing), then merged as `dc40ca9`. Follow-up in the
same session:
- `LessonInputPolicy.RESERVED_CATEGORY` is now the single value; `ModelLessonsList.ALL_CATEGORIES`
  refers to it, so the rule and the filter cannot drift apart.
- MOD-21 and MOD-22 relabelled CS-11 (keyword rules); full Javadoc (How, Pass when) added to
  MOD-21 to MOD-23.
- MOD-24 added (CS-5, positive): "All", "Categories", "All categories list" are accepted and a
  lesson under "All" can be filtered on its own.
- Counts updated in `TP2TestRunner`, `testingAutomation/package-info.java`, the map, the
  TestingScreencast script, and the README; `working/build_all.sh` rerun.

Result: 76/76 automated (83 with the manual procedures), TP1 runner passing, 0 broken Javadoc
links, 151 code screenshots. Screencast Guide page numbers unchanged, so the remote-control
prompt still matches.
