# CSE 360 TP2 — Team 24

`TP2/` is the submission folder: zip it as-is for Canvas (`zip -r TP2.zip TP2 -x '*.DS_Store'`).
The three main screencasts and six Status Summary screencasts still have to be added before upload.

`working/` is not submitted. It holds what generates the deliverables from the source:

- `build_all.sh` regenerates the Javadoc folders, every screenshot, and every PDF.
- `build_javadoc.sh`, `screenshots/make_screenshots.py` are the two halves of that.
- `latex/` holds the LaTeX sources of the PDFs; `make_plans.py` and `make_map.py` generate the
  Plan and Status files and the Requirements to Tests Map tables.
- `scripts/Screencast Guide.pdf` lists, per screencast, the requirements and the exact files to show.

Requires JDK 25, JavaFX SDK 25.0.4, the H2 jar, a TeX Live install, and Google Chrome. The
JavaFX and H2 paths are set at the top of `build_javadoc.sh` and `screenshots/make_screenshots.py`
(default `~/Projects/ASU-Foundations-F26/tools/`).

Run the tests: `testingAutomation.TP2TestRunner` (76 automated tests). Manual procedures:
`testingAutomation.LessonsManualTestProcedures`.
