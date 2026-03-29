#!/usr/bin/env python3
from pathlib import Path

SRC = Path('data/google-10000-english.txt')
OUT = Path('app/src/main/assets/english_words.txt')
MAX_WORDS = 12000


def valid(word: str) -> bool:
    if len(word) < 2 or len(word) > 24:
        return False
    return all(ch.isalpha() or ch == "'" for ch in word)


def main() -> None:
    if not SRC.exists():
        raise SystemExit(f'missing source file: {SRC}')
    seen = set()
    words = []
    for line in SRC.read_text(encoding='utf-8').splitlines():
        word = line.strip().lower()
        if not valid(word) or word in seen:
            continue
        seen.add(word)
        words.append(word)
        if len(words) >= MAX_WORDS:
            break
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text('\n'.join(words) + '\n', encoding='utf-8')
    print(f'wrote {OUT} with {len(words)} words')


if __name__ == '__main__':
    main()
