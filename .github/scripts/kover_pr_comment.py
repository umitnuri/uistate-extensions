#!/usr/bin/env python3
"""Publish (or update) a Kover coverage summary as a pull request comment.

Reads the JaCoCo-format XML produced by `koverXmlReport`, renders a markdown
table with overall and per-package line/branch coverage, and posts it via the
`gh` CLI. An HTML marker in the comment body makes reruns update in place.
"""
import os
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

MARKER = "<!-- kover-code-coverage -->"
DEFAULT_XML = "build/reports/kover/report.xml"


def run(cmd, check=True):
    return subprocess.run(cmd, capture_output=True, text=True, check=check)


def ratio(counters, ctype):
    counter = counters.get(ctype)
    if counter is None:
        return 100.0, ""
    missed, covered = int(counter.get("missed", 0)), int(counter.get("covered", 0))
    total = missed + covered
    if total == 0:
        return 100.0, ""
    return 100.0 * covered / total, f" ({covered}/{total})"


def counters_of(element):
    return {c.get("type"): c for c in element.findall("counter")}


def build_body(xml_path):
    path = Path(xml_path)
    if not path.exists():
        return (
            f"{MARKER}\n"
            "### 🧪 Code coverage\n\n"
            f"Coverage report not found at `{xml_path}` — the coverage step likely failed before generating it."
        )

    root = ET.parse(path).getroot()
    overall = counters_of(root)
    line_pct, line_detail = ratio(overall, "LINE")
    branch_pct, branch_detail = ratio(overall, "BRANCH")
    emoji = "🟢" if line_pct >= 100 else ("🟡" if line_pct >= 80 else "🔴")

    lines = [
        MARKER,
        "### 🧪 Code coverage",
        "",
        f"{emoji} **Line: {line_pct:.1f}%**{line_detail} · **Branch: {branch_pct:.1f}%**{branch_detail}",
        "",
        "| Package | Line | Branch |",
        "|---|---:|---:|",
    ]
    for pkg in root.findall("package"):
        counters = counters_of(pkg)
        if "LINE" not in counters:
            continue
        p_line, d_line = ratio(counters, "LINE")
        p_branch, d_branch = ratio(counters, "BRANCH")
        branch_text = f"{p_branch:.1f}%{d_branch}" if d_branch or "BRANCH" in counters else "—"
        lines.append(f"| `{pkg.get('name')}` | {p_line:.1f}%{d_line} | {branch_text} |")

    lines += [
        "",
        f"Gate: 100% line coverage via `./gradlew koverVerify` (demo UI and generated `*.extensions.*` code excluded).",
    ]
    return "\n".join(lines)


def main():
    xml_path = sys.argv[1] if len(sys.argv) > 1 else DEFAULT_XML
    body = build_body(xml_path)

    if os.environ.get("GITHUB_EVENT_NAME") != "pull_request":
        print(body)
        return

    repo = os.environ["GITHUB_REPOSITORY"]
    pr = os.environ.get("GITHUB_EVENT_PULL_REQUEST_NUMBER")
    if not pr:
        pr = run(["gh", "pr", "view", "--json", "number", "-q", ".number"]).stdout.strip()

    find = run(
        [
            "gh", "api", f"repos/{repo}/issues/{pr}/comments", "--paginate", "-q",
            f'.[] | select(.body | contains("{MARKER}")) | .id',
        ],
        check=False,
    )
    comment_ids = [line.strip() for line in find.stdout.splitlines() if line.strip()]

    if comment_ids:
        run(["gh", "api", "-X", "PATCH", f"repos/{repo}/issues/comments/{comment_ids[0]}",
             "-f", f"body={body}"])
    else:
        run(["gh", "pr", "comment", pr, "--body", body])

    print(body)


if __name__ == "__main__":
    main()
