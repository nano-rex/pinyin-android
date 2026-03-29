#!/usr/bin/env python3
import gzip
import re
from collections import defaultdict
from pathlib import Path

SRC = Path('data/cedict_1_0_ts_utf-8_mdbg.txt.gz')
OUT = Path('app/src/main/assets/pinyin_lexicon.tsv')
PATTERN = re.compile(r'^(\S+)\s+(\S+)\s+\[([^\]]+)\]')
MAX_PER_KEY = 64


def normalize(pinyin: str) -> str:
    pinyin = pinyin.lower().replace('u:', 'v').replace('ü', 'v')
    pinyin = re.sub(r'\d', '', pinyin)
    pinyin = re.sub(r'\s+', '', pinyin)
    pinyin = re.sub(r"[^a-z']", '', pinyin)
    return pinyin


def main() -> None:
    if not SRC.exists():
        raise SystemExit(f'missing source file: {SRC}')

    buckets = defaultdict(lambda: {'simp': [], 'trad': [], 'simp_seen': set(), 'trad_seen': set()})
    with gzip.open(SRC, 'rt', encoding='utf-8') as handle:
        for line in handle:
            if not line or line.startswith('#'):
                continue
            match = PATTERN.match(line)
            if not match:
                continue
            trad, simp, pinyin = match.group(1), match.group(2), match.group(3)
            key = normalize(pinyin)
            if not key:
                continue
            bucket = buckets[key]
            if simp not in bucket['simp_seen']:
                bucket['simp_seen'].add(simp)
                bucket['simp'].append(simp)
            if trad not in bucket['trad_seen']:
                bucket['trad_seen'].add(trad)
                bucket['trad'].append(trad)

    OUT.parent.mkdir(parents=True, exist_ok=True)
    with OUT.open('w', encoding='utf-8') as handle:
        for key in sorted(buckets.keys()):
            bucket = buckets[key]
            bucket['simp'].sort(key=lambda value: (-len(value), value))
            bucket['trad'].sort(key=lambda value: (-len(value), value))
            simp = '|'.join(bucket['simp'][:MAX_PER_KEY])
            trad = '|'.join(bucket['trad'][:MAX_PER_KEY])
            handle.write(f'{key}\t{simp}\t{trad}\n')

    print(f'wrote {OUT} with {len(buckets)} entries')


if __name__ == '__main__':
    main()
