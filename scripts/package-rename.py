#!/usr/bin/env python3
"""
Bulk-renames a Java package throughout a source tree:
  - Moves/renames folder structure (cn/nukkit/... -> org/powernukkitx/...)
  - Updates package declarations, import statements, and any other
    textual references to the old package inside all files (.java, .kt,
    .xml, .properties, .gradle, .yml, etc.)

Usage:
    python3 rename_package.py /path/to/project [--old cn.nukkit] [--new org.powernukkitx] [--dry-run]

By default it operates on the whole project directory recursively.
It skips common build/VCS directories (.git, build, target, out, .gradle, .idea).
"""

import argparse
import os
import shutil
import sys

# Directories we never want to touch
SKIP_DIRS = {".git", "build", "target", "out", ".gradle", ".idea", "node_modules"}

# File extensions considered "text" and safe to search/replace inside.
# Add more if your project uses other text formats.
TEXT_EXTENSIONS = {
    ".java", ".kt", ".kts", ".xml", ".properties", ".gradle",
    ".yml", ".yaml", ".md", ".txt", ".json", ".MF", ".cfg", ".ini",
}


def should_skip_dir(dirname: str) -> bool:
    return dirname in SKIP_DIRS or dirname.startswith(".")


def is_text_file(path: str) -> bool:
    _, ext = os.path.splitext(path)
    if ext in TEXT_EXTENSIONS:
        return True
    # Files with no extension (e.g. MANIFEST.MF handled above) — be conservative
    return False


def replace_in_file(path: str, old: str, old_slash: str, new: str, new_slash: str, dry_run: bool) -> bool:
    """Replace textual occurrences of the old package (dotted and slashed form)
    with the new one. Returns True if the file was changed."""
    try:
        with open(path, "r", encoding="utf-8") as f:
            content = f.read()
    except (UnicodeDecodeError, PermissionError):
        return False

    new_content = content.replace(old, new).replace(old_slash, new_slash)

    if new_content != content:
        if not dry_run:
            with open(path, "w", encoding="utf-8") as f:
                f.write(new_content)
        return True
    return False


def move_package_folders(root: str, old_slash: str, new_slash: str, dry_run: bool):
    """Find every directory path ending in the old package's folder structure
    (e.g. .../cn/nukkit) and move it to the new location
    (e.g. .../org/powernukkitx), preserving whatever subtree lives under it."""
    moved = []

    for dirpath, dirnames, _filenames in os.walk(root, topdown=True):
        # prune skip dirs in-place so os.walk doesn't descend into them
        dirnames[:] = [d for d in dirnames if not should_skip_dir(d)]

        norm = dirpath.replace(os.sep, "/")
        if norm.endswith("/" + old_slash) or norm == old_slash:
            new_dir = norm[: -len(old_slash)] + new_slash
            new_dir = new_dir.replace("/", os.sep)
            old_dir = dirpath

            print(f"MOVE  {old_dir}  ->  {new_dir}")
            if not dry_run:
                os.makedirs(os.path.dirname(new_dir), exist_ok=True)
                shutil.move(old_dir, new_dir)
            moved.append((old_dir, new_dir))

    return moved


def walk_and_replace_contents(root: str, old: str, old_slash: str, new: str, new_slash: str, dry_run: bool):
    changed_files = []
    for dirpath, dirnames, filenames in os.walk(root, topdown=True):
        dirnames[:] = [d for d in dirnames if not should_skip_dir(d)]
        for fname in filenames:
            fpath = os.path.join(dirpath, fname)
            if not is_text_file(fpath):
                continue
            if replace_in_file(fpath, old, old_slash, new, new_slash, dry_run):
                changed_files.append(fpath)
                print(f"EDIT  {fpath}")
    return changed_files


def rename_matching_filenames(root: str, old_last: str, new_last: str, dry_run: bool):
    """Optional: if any filenames themselves contain the last package
    segment (rare, but e.g. 'NukkitMain.java' referencing 'nukkit'),
    this is left untouched by default — package renames only affect
    folders/contents, not arbitrary filenames. Included as a no-op hook
    in case you want to extend it."""
    pass


def main():
    parser = argparse.ArgumentParser(description="Bulk rename a Java package.")
    parser.add_argument("project_root", help="Root directory of the project to process")
    parser.add_argument("--old", default="cn.nukkit", help="Old package name (dotted)")
    parser.add_argument("--new", default="org.powernukkitx", help="New package name (dotted)")
    parser.add_argument("--dry-run", action="store_true", help="Show what would change without modifying anything")
    args = parser.parse_args()

    root = os.path.abspath(args.project_root)
    if not os.path.isdir(root):
        print(f"Error: {root} is not a directory", file=sys.stderr)
        sys.exit(1)

    old, new = args.old, args.new
    old_slash, new_slash = old.replace(".", "/"), new.replace(".", "/")

    print(f"Project root : {root}")
    print(f"Old package  : {old}  ({old_slash})")
    print(f"New package  : {new}  ({new_slash})")
    print(f"Dry run      : {args.dry_run}")
    print("-" * 60)

    # Step 1: rewrite file contents (package decls, imports, strings, configs...)
    print("\n== Step 1: Updating file contents ==")
    changed = walk_and_replace_contents(root, old, old_slash, new, new_slash, args.dry_run)

    # Step 2: move folder structure to match new package
    print("\n== Step 2: Moving folder structure ==")
    moved = move_package_folders(root, old_slash, new_slash, args.dry_run)

    print("-" * 60)
    print(f"Files edited : {len(changed)}")
    print(f"Dirs moved   : {len(moved)}")
    if args.dry_run:
        print("\nThis was a DRY RUN — nothing was actually changed.")
    else:
        print("\nDone.")


if __name__ == "__main__":
    main()
