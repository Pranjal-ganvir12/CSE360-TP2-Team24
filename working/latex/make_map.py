#!/usr/bin/env python3
"""Builds the tables of the Requirements to Tests Map from the test source itself.

Each test method's Javadoc starts with "<p> ID (CS-n, positive). ..." (or two ids for a paired
method), so the requirement and polarity of every test are read from the code, and the HTML
anchor and screenshot name are derived the same way the Javadoc tool and make_screenshots.py
derive them.  Nothing in the map is typed twice."""
import os, re, csv

HERE = os.path.dirname(os.path.abspath(__file__))
SRC = os.path.normpath(os.path.join(HERE, "..", "..", "TP2", "Foundations-F26", "src", "testingAutomation"))
SHOTS = os.path.normpath(os.path.join(HERE, "..", "screenshots", "test_shots.tsv"))
SUITES = ["LessonValidationTestingAutomation", "LessonsCrudTestingAutomation",
          "LessonsListTestingAutomation", "LessonsModelTestingAutomation",
          "LessonsManualTestProcedures"]

REQS = [
 ("CS-1", "A contributor can create a lesson with a title, category, description, and optional resolution and keywords. The contributor is set from the logged-in user."),
 ("CS-2", "A contributor can see all lessons, newest first. An empty list is explained in words, not shown as a blank screen."),
 ("CS-3", "A contributor can open one lesson and see all of its fields."),
 ("CS-4", "A contributor can search lessons. The search covers title, description, and keywords and ignores case. No match is explained in words."),
 ("CS-5", "A contributor can filter lessons by category. An empty category is explained in words. The filter's label \u201cAll categories\u201d cannot be used as a category."),
 ("CS-6", "A contributor can view only their own lessons. If they have none, a message says so."),
 ("CS-7", "A contributor can update a lesson they contributed. An update is validated the same way as a new lesson."),
 ("CS-8", "A contributor can delete a lesson they contributed, after confirming. It is removed for all users."),
 ("CS-9", "Only the contributor of a lesson can update or delete it. Any other user is refused with a message."),
 ("CS-10", "Title, category, description, and contributor are required; resolution and keywords are optional. A missing required value is refused with a message naming the field."),
 ("CS-11", "Length limits: title 80, category 30, description 2000, resolution 2000, keywords 200 characters, at most 10 keywords of 30 characters each. A value over a limit, or an empty keyword entry, is refused with a message naming the problem."),
 ("CS-12", "Lessons persist across application restarts, including on a database left by an HW2 version."),
]

shot_of = {}
with open(SHOTS) as f:
    for ids, fname, cls in csv.reader(f, delimiter="\t"):
        for i in ids.split(","):
            shot_of[i] = fname

tests = []   # (id, suite, method, [CS], polarity, summary)
for suite in SUITES:
    text = open(os.path.join(SRC, suite + ".java"), encoding="utf-8").read()
    for m in re.finditer(r"/\*\*\s*\n\s*\*\s*<p>(.*?)</p>(.*?)\*/\s*\n\s*private static void (\w+)\(", text, re.S):
        first, method = " ".join(m.group(1).replace("*", " ").split()), m.group(3)
        for tid, inside in re.findall(r"((?:VAL|CRUD|LIST|MOD|MAN)-\d\d) \(([^)]*)\)", first):
            cs = ["CS-" + n for n in re.findall(r"CS-(\d+)", inside) + re.findall(r"and (\d+)\b", inside)]
            cs = sorted(set(cs), key=lambda c: int(c[3:]))
            pol = "positive and negative" if "positive and negative" in inside else \
                  ("positive" if "positive" in inside else "negative")
            summary = re.split(r"\)\.\s+", first)[-1] if first.count(")") == 1 else ""
            tests.append((tid, suite, method, cs, pol, summary))

tests.sort(key=lambda t: (["VAL", "CRUD", "LIST", "MOD", "MAN"].index(t[0].split("-")[0]), t[0]))
ids = [t[0] for t in tests]
assert len(ids) == len(set(ids)), "duplicate test id"
for t in tests:
    assert t[0] in shot_of, f"no screenshot for {t[0]}"


def esc(s):
    return s.replace("_", r"\_").replace("&", r"\&").replace("#", r"\#").replace("%", r"\%")


def idlist(xs):
    return ", ".join(xs) if xs else "--"


out = []
# Table 1: requirement -> tests -> HTML pages -> screenshots.
out.append(r"""\setlength{\tabcolsep}{4pt}\begin{longtable}{@{}>{\raggedright\arraybackslash}p{0.055\linewidth} >{\raggedright\arraybackslash}p{0.33\linewidth} >{\raggedright\arraybackslash}p{0.14\linewidth} >{\raggedright\arraybackslash}p{0.15\linewidth} >{\raggedright\arraybackslash}p{0.245\linewidth}@{}}
\toprule
\textbf{Req.} & \textbf{Requirement} & \textbf{Positive tests} & \textbf{Negative tests} & \textbf{HTML pages} \\
\midrule
\endhead""")
for rid, text in REQS:
    pos = [t[0] for t in tests if rid in t[3] and "positive" in t[4]]
    neg = [t[0] for t in tests if rid in t[3] and "negative" in t[4]]
    assert pos and neg, f"{rid} needs positive and negative tests ({pos}, {neg})"
    html = sorted({t[1] + ".html" for t in tests if rid in t[3]})
    out.append(r"\textbf{%s} & %s & %s & %s & \scriptsize %s \\ \addlinespace[3pt]"
               % (rid, esc(text), idlist(pos), idlist(neg), r" \newline ".join(esc(h) for h in html)))
out.append(r"\bottomrule\end{longtable}")
with open(os.path.join(HERE, "map_requirements.tex"), "w") as f:
    f.write("\n".join(out))

# Table 2: every test, its method and anchor, and its screenshot.
out = [r"""\setlength{\tabcolsep}{4pt}\begin{longtable}{@{}>{\raggedright\arraybackslash}p{0.07\linewidth} >{\raggedright\arraybackslash}p{0.08\linewidth} >{\raggedright\arraybackslash}p{0.07\linewidth} >{\raggedright\arraybackslash}p{0.33\linewidth} >{\raggedright\arraybackslash}p{0.37\linewidth}@{}}
\toprule
\textbf{Test} & \textbf{Req.} & \textbf{Kind} & \textbf{Method (anchor in the HTML page)} & \textbf{Screenshot} \\
\midrule
\endhead"""]
current = None
for tid, suite, method, cs, pol, _ in tests:
    if suite != current:
        out.append(r"\multicolumn{5}{@{}l}{\sffamily\bfseries\small %s.html} \\" % suite)
        current = suite
    kind = {"positive": "pos.", "negative": "neg.", "positive and negative": "pos. + neg."}[pol]
    out.append(r"%s & %s & %s & \scriptsize\texttt{\#%s()} & \scriptsize\texttt{%s} \\"
               % (tid, ", ".join(cs), kind, esc(method), esc(shot_of[tid])))
out.append(r"\bottomrule\end{longtable}")
with open(os.path.join(HERE, "map_tests.tex"), "w") as f:
    f.write("\n".join(out))

auto = [t for t in tests if not t[0].startswith("MAN")]
with open(os.path.join(HERE, "map_counts.tex"), "w") as f:
    f.write(r"\newcommand{\nauto}{%d}\newcommand{\nman}{%d}\newcommand{\ntotal}{%d}"
            % (len(auto), len(tests) - len(auto), len(tests)))
print(len(tests), "tests mapped;", len(auto), "automated")
