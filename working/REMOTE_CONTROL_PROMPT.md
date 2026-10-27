# Prompt: TP2 Presenter Remote

Copy everything below the line into the AI tool that will build the remote.

---

Build a small local "presenter remote" for recording our CSE 360 TP2 screencasts. When I click a
team member, it lists everything that member needs open for their part, in the order they present
it. Each item is clickable and opens that file or runs that command from our TP2_FIXED folder.

## Environment
- macOS, Python 3 from the standard library only (no pip installs), Google Chrome installed.
- Project root: `~/Downloads/TP2_FIXED` by default, overridable with `--root PATH`. All paths below
  are relative to this root. Paths contain spaces; quote and escape them properly.
- Java: JDK 25 on PATH. JavaFX SDK lib: `~/Projects/ASU-Foundations-F26/tools/javafx-sdk-25.0.4/lib`.
  H2 jar: `~/Projects/ASU-Foundations-F26/tools/h2-2.3.232.jar`. Both overridable with `--fx` and `--h2`.

## What to build
1. `working/remote/presenter_remote.py`: one script that starts a local web server on
   `127.0.0.1` (never `0.0.0.0`) on a free port and opens the UI in Chrome.
2. `working/remote/manifest.json`: the members, segments, and items listed below. The script
   reads it at start-up, so the list can be edited without touching code.
3. `working/remote/README.md`: how to start it, in five lines or fewer.

## UI behaviour
- Left column: five member buttons (Arush Ugal, Karan Patel, Anirudh B, Pranjal Ganvir, Moksha Dev).
- Clicking a member shows that member's segments (for example "TestingScreencast, part 1" or
  "Status Summary 2026-10-22"), each with its target length.
- Clicking a segment shows its items as a numbered checklist. Each item shows a short label,
  the relative path or command, an **Open** button, and a tick box the presenter checks once it's
  done. There's also an **Open next** button that opens the first unticked item and ticks it, so a
  presenter can step through the segment like a slide clicker.
- At start-up, check that every file in the manifest exists. Show a missing file in red with its
  path, and disable its Open button. Never fail silently.
- Large, readable buttons. Small enough to keep in a corner of the screen while recording.

## How each item type opens
- `pdf`: open in Chrome at the given page: `file://<abs path>#page=N`.
- `html`: open in Chrome; keep any `#anchor` (URL-encode parentheses in Javadoc anchors such as
  `#crud11And12PersistAcrossReopen()`).
- `png`: open in Preview (`open -a Preview`).
- `command`: run in a new Terminal window via `osascript`, so the output is visible on screen.
  Commands are fixed in the manifest; the server must not accept or run any command or path
  sent from the browser. The browser sends only an item id, and the server looks it up.
- `note`: no file. Shows the instruction text only (for example "switch to Eclipse").

## Security
- Bind to 127.0.0.1 only. Use a random token in the page URL and require it on every request.
- Open only paths listed in the manifest, after resolving them and confirming they stay inside
  the project root (no `..` escapes, no symlinks out).

## Commands (item type `command`)
- `build`: compile into `/tmp/tp2remote/bin`:
  `javac --module-path FX --add-modules javafx.controls -cp H2 -d /tmp/tp2remote/bin $(find "ROOT/TP2/Foundations-F26/src" -name '*.java')`,
  then copy `TP2/Foundations-F26/src/applicationMain/application.css` to
  `/tmp/tp2remote/bin/applicationMain/`.
- `tests`: build if needed, then run
  `java --module-path FX:/tmp/tp2remote/bin --add-modules javafx.controls -cp H2 -m FoundationsF26/testingAutomation.TP2TestRunner`.
- `app`: build if needed, then run `FoundationsMain` the same way with
  `-Duser.home=/tmp/tp2remote/home`, so it uses its own database and never touches the real
  `~/FoundationDatabase`.
- `manual`: build if needed, then run `testingAutomation.LessonsManualTestProcedures` the same way
  (it reads P/F/S answers from the Terminal).

## Manifest contents
Base folders: `LL = TP2/Lessons_Learned_Javadocs`, `GUI = TP2/Contributor_Javadocs`,
`TST = TP2/ContributorTests_Javadocs`, `GUIDE = working/scripts/Screencast Guide.pdf`,
`PLAN = TP2/TP2 Planning and Tracking`.

### Arush Ugal
**LessonsLearnedScreencast, part 1 (3.5 min)**
1. pdf `GUIDE` page 7: my script
2. pdf `GUIDE` page 1: Task 3 requirements table
3. png `LL/3.1-entityClasses-package-source-tree.png`
4. png `LL/3.1-entityClasses-package-declarations.png`
5. png `LL/3.1-entityClasses-package-summary-javadoc.png`
6. html `LL/entityClasses/LessonsLearned.html`
7. png `LL/3.4-LessonsLearned-class-javadoc.png`
8. png `LL/3.3-LessonsLearned-attributes-contributor-stories.png`
9. png `LL/3.3-LessonsLearned-attributes-TP3-epics.png`
10. png `LL/3.2-LessonsLearned-create.png`
11. png `LL/3.2-LessonsLearned-read-and-update.png`
12. png `LL/3.2-LessonsLearned-delete-and-validate.png`
13. png `LL/3.4-LessonsLearned-constructors.png`
14. png `LL/3.5-LessonsLearned-no-setters-for-owner-dates-TP3.png`
15. png `LL/3.5-LessonsLearned-isOwnedBy-and-matches.png`

**Status Summary 2026-10-12 (3.5 min)**
1. pdf `GUIDE` page 13: my script
2. pdf `PLAN/Plan and Status 2026-10-12.pdf` page 1: task board
3. pdf `PLAN/Plan and Status 2026-10-12.pdf` page 2: in-progress details
4. note: "Show each member's standup slide (slides are not in the folder; add their paths here)."

**Status Summary 2026-10-24 (3.5 min)**
1. pdf `GUIDE` page 19: my script
2. pdf `PLAN/Plan and Status 2026-10-24.pdf` page 1
3. pdf `PLAN/Plan and Status 2026-10-24.pdf` page 2
4. note: same slide note as above

### Anirudh B
**LessonsLearnedScreencast, part 2 (2.5 min)**
1. pdf `GUIDE` page 7: my script (starts below Arush's part, continues on page 8)
2. html `LL/entityClasses/LessonsLearnedList.html`
3. png `LL/3.4-LessonsLearnedList-class-javadoc.png`
4. png `LL/3.3-LessonsLearnedList-attribute.png`
5. png `LL/3.2-LessonsLearnedList-readAll-and-createLesson.png`
6. png `LL/3.2-LessonsLearnedList-updateLesson-and-deleteLesson.png`
7. png `LL/3.2-LessonsLearnedDatabase-addLesson.png`
8. png `LL/3.4-LessonsLearnedList-subsets.png`
9. png `LL/3.4-LessonsLearnedList-contributor-and-categories.png`
10. png `LL/3.5-LessonsLearnedDatabase-createTables.png`
11. png `LL/3.5-LessonsLearnedDatabase-moveLegacyTableAside.png`
12. png `LL/3.5-LessonsLearnedDatabase-getAllLessons-and-updateLesson.png`
13. png `LL/3.5-LessonInputPolicy-checkLongText-HW2-defect.png`
14. png `LL/3.5-LessonsLearnedList-getAll-returns-copy.png`

**TestingScreencast, part 2 (5.5 min)**
1. pdf `GUIDE` page 12: my script
2. note: "Scroll the TP2TestRunner output Pranjal started to the List block, then Model, then the combined totals."
3. png `TST/7.4-TP2TestRunner-output-3-List.png` (backup if the live window is closed)
4. png `TST/7.4-TP2TestRunner-output-4-Model.png` (backup)
5. png `TST/7.4-TP2TestRunner-output-5-Combined.png` (backup)
6. command `app`: the application with a fresh database
7. command `manual`: the manual procedures MAN-01 to MAN-07

**Status Summary 2026-10-16 (3.5 min)**
1. pdf `GUIDE` page 15: my script
2. pdf `PLAN/Plan and Status 2026-10-16.pdf` page 1
3. pdf `PLAN/Plan and Status 2026-10-16.pdf` page 2
4. note: slide note

### Karan Patel
**PhaseTwoGUIScreencast, part 1 (4.5 min)**
1. pdf `GUIDE` page 9: my script
2. pdf `GUIDE` page 2: Task 4 requirements table
3. png `GUI/4.1-Contributor-packages-overview-javadoc.png`
4. png `GUI/4.1-gui-packages-source-tree.png`
5. png `GUI/4.1-guiLessonsList-package-classes-javadoc.png`
6. png `GUI/4.1-guiLessonEditor-package-classes-javadoc.png`
7. png `GUI/4.1-guiLessonDetail-package-classes-javadoc.png`
8. png `GUI/4.2-guiLessonsList-View-wires-Controller.png`
9. png `GUI/4.2-guiLessonsList-Controller-asks-Model.png`
10. html `GUI/guiLessonsList/ModelLessonsList.html`
11. png `GUI/4.4-ModelLessonsList-attributes.png`
12. png `GUI/4.4-ViewLessonsList-attributes.png`
13. png `GUI/4.4-ViewLessonsList-class-javadoc.png`
14. png `GUI/4.4-ControllerLessonsList-class-javadoc.png`
15. png `GUI/4.4-ModelLessonsList-class-javadoc.png`
16. png `GUI/4.5-ModelLessonsList-applyFilters.png`
17. png `GUI/4.5-ModelLessonsList-statusMessage.png`
18. png `GUI/4.5-ControllerLessonsList-reloadLessons-fillCategories.png`
19. png `GUI/4.4-guiRole1-Lessons-Learned-button.png`
20. png `GUI/4.4-guiRole1-performLessonsLearned.png`
21. png `GUI/4.4-guiRole2-performLessonsLearned.png`

**Status Summary 2026-10-14 (3.5 min)**
1. pdf `GUIDE` page 14: my script
2. pdf `PLAN/Plan and Status 2026-10-14.pdf` page 1
3. pdf `PLAN/Plan and Status 2026-10-14.pdf` page 2
4. note: slide note

### Moksha Dev
**PhaseTwoGUIScreencast, part 2 (4.5 min)**
1. pdf `GUIDE` page 9: my script (starts below Karan's part, continues on page 10)
2. html `GUI/guiLessonEditor/package-summary.html`
3. png `GUI/4.4-guiLessonEditor-package-info.png`
4. png `GUI/4.2-guiLessonEditor-View-and-Controller.png`
5. png `GUI/4.4-ViewLessonEditor-attributes.png`
6. png `GUI/4.4-ModelLessonEditor-class-javadoc.png`
7. png `GUI/4.5-ModelLessonEditor-saveNewLesson.png`
8. png `GUI/4.5-ModelLessonEditor-saveChanges.png`
9. png `GUI/4.5-ModelLessonEditor-tidyKeywords.png`
10. html `GUI/guiLessonDetail/package-summary.html`
11. png `GUI/4.4-ViewLessonDetail-attributes.png`
12. png `GUI/4.4-ViewLessonDetail-displayLessonDetail.png`
13. png `GUI/4.4-ModelLessonDetail-canChange-ownershipNote.png`
14. png `GUI/4.2-guiLessonDetail-View-and-Controller.png`
15. png `GUI/4.5-ModelLessonDetail-deleteLesson.png`
16. png `GUI/4.5-FoundationsMain-lessonsDatabase.png`
17. command `app`: show the list, a lesson, and the editor for about 30 seconds

**Status Summary 2026-10-19 (3.5 min)**
1. pdf `GUIDE` page 16: my script (continues on page 17)
2. pdf `PLAN/Plan and Status 2026-10-19.pdf` page 1
3. pdf `PLAN/Plan and Status 2026-10-19.pdf` page 2
4. note: slide note

### Pranjal Ganvir
**TestingScreencast, part 1 (6.5 min)**
1. pdf `GUIDE` page 11: my script
2. pdf `GUIDE` page 3: Task 7 requirements table
3. pdf `TP2/Requirements to Tests Map.pdf` page 1: requirements and their tests
4. pdf `TP2/Requirements to Tests Map.pdf` page 2: every test, its HTML, its screenshot
5. html `TST/testingAutomation/LessonsCrudTestingAutomation.html#crud11And12PersistAcrossReopen()`
6. png `TST/7.4-CRUD-11-CRUD-12-PersistAcrossReopen.png`
7. png `TST/7.4-check-helper-and-refusedWith.png`
8. command `tests`: live run of TP2TestRunner (Validation, then CRUD)
9. png `TST/7.4-TP2TestRunner-output-1-Validation.png` (backup)
10. png `TST/7.4-TP2TestRunner-output-2-CRUD.png` (backup)

**Status Summary 2026-10-22 (3.5 min)**
1. pdf `GUIDE` page 18: my script
2. pdf `PLAN/Plan and Status 2026-10-22.pdf` page 1
3. pdf `PLAN/Plan and Status 2026-10-22.pdf` page 2
4. note: slide note

## Done when
- `python3 working/remote/presenter_remote.py` opens the UI with no errors and every manifest
  path shows as present.
- Clicking Open on one item of each type works: pdf (at the right page), html (with anchor),
  png, command, note.
- A request without the token, or with an item id not in the manifest, is refused.
